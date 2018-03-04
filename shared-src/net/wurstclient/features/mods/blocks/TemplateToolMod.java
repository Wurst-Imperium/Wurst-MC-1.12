/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.blocks;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.TreeSet;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.wurstclient.compatibility.WBlock;
import net.wurstclient.compatibility.WMath;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.GUIRenderListener;
import net.wurstclient.events.RenderListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Feature;
import net.wurstclient.features.Mod;
import net.wurstclient.files.WurstFolders;
import net.wurstclient.font.Fonts;
import net.wurstclient.utils.ChatUtils;
import net.wurstclient.utils.JsonUtils;
import net.wurstclient.utils.RenderUtils;

@Mod.Bypasses
public final class TemplateToolMod extends Mod
	implements UpdateListener, RenderListener, GUIRenderListener
{
	private Step step;
	private BlockPos posLookingAt;
	private Area area;
	private Template template;
	private File file;
	
	public TemplateToolMod()
	{
		super("TemplateTool",
			"Allows you to create custom templates for AutoBuild by scanning existing buildings.");
		setCategory(Category.BLOCKS);
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.mods.autoBuildMod};
	}
	
	@Override
	public void onEnable()
	{
		// disable BowAimbot because it displays its status in the same location
		// as TemplateTool
		if(wurst.mods.bowAimbotMod.isEnabled())
			wurst.mods.bowAimbotMod.setEnabled(false);
		
		step = Step.START_POS;
		
		wurst.events.add(UpdateListener.class, this);
		wurst.events.add(RenderListener.class, this);
		wurst.events.add(GUIRenderListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
		wurst.events.remove(RenderListener.class, this);
		wurst.events.remove(GUIRenderListener.class, this);
		
		for(Step step : Step.values())
			step.pos = null;
		posLookingAt = null;
		area = null;
		template = null;
		file = null;
	}
	
	@Override
	public void onUpdate()
	{
		// select position steps
		if(step.selectPos)
		{
			// continue with next step
			if(step.pos != null && Keyboard.isKeyDown(Keyboard.KEY_RETURN))
			{
				step = Step.values()[step.ordinal() + 1];
				
				// delete posLookingAt
				if(!step.selectPos)
					posLookingAt = null;
				
				return;
			}
			
			if(mc.objectMouseOver != null
				&& mc.objectMouseOver.getBlockPos() != null)
			{
				// set posLookingAt
				posLookingAt = mc.objectMouseOver.getBlockPos();
				
				// offset if sneaking
				if(mc.gameSettings.keyBindSneak.pressed)
					posLookingAt =
						posLookingAt.offset(mc.objectMouseOver.sideHit);
				
			}else
				posLookingAt = null;
			
			// set selected position
			if(posLookingAt != null && mc.gameSettings.keyBindUseItem.pressed)
				step.pos = posLookingAt;
			
			// scanning area step
		}else if(step == Step.SCAN_AREA)
		{
			// initialize area
			if(area == null)
			{
				area = new Area(Step.START_POS.pos, Step.END_POS.pos);
				Step.START_POS.pos = null;
				Step.END_POS.pos = null;
			}
			
			// scan area
			for(int i = 0; i < area.scanSpeed && area.iterator.hasNext(); i++)
			{
				area.scannedBlocks++;
				BlockPos pos = area.iterator.next();
				
				if(!WBlock.getMaterial(pos).isReplaceable())
					area.blocksFound.add(pos);
			}
			
			// update progress
			area.progress = (float)area.scannedBlocks / (float)area.totalBlocks;
			
			// continue with next step
			if(!area.iterator.hasNext())
				step = Step.values()[step.ordinal() + 1];
			
			// creating template step
		}else if(step == Step.CREATE_TEMPLATE)
		{
			// initialize template
			if(template == null)
				template =
					new Template(Step.FIRST_BLOCK.pos, area.blocksFound.size());
			
			// sort blocks by distance
			if(!area.blocksFound.isEmpty())
			{
				// move blocks to TreeSet
				int min =
					Math.max(0, area.blocksFound.size() - template.scanSpeed);
				for(int i = area.blocksFound.size() - 1; i >= min; i--)
				{
					BlockPos pos = area.blocksFound.get(i);
					template.remainingBlocks.add(pos);
					area.blocksFound.remove(i);
				}
				
				// update progress
				template.progress = (float)template.remainingBlocks.size()
					/ (float)template.totalBlocks;
				
				return;
			}
			
			// add closest block first
			if(template.sortedBlocks.isEmpty()
				&& !template.remainingBlocks.isEmpty())
			{
				BlockPos first = template.remainingBlocks.first();
				template.sortedBlocks.add(first);
				template.remainingBlocks.remove(first);
				template.lastAddedBlock = first;
			}
			
			// add remaining blocks
			for(int i = 0; i < template.scanSpeed
				&& !template.remainingBlocks.isEmpty(); i++)
			{
				BlockPos current = template.remainingBlocks.first();
				double dCurrent = Double.MAX_VALUE;
				
				for(BlockPos pos : template.remainingBlocks)
				{
					double dPos = template.lastAddedBlock.distanceSq(pos);
					if(dPos >= dCurrent)
						continue;
					
					for(EnumFacing facing : EnumFacing.values())
					{
						BlockPos next = pos.offset(facing);
						if(!template.sortedBlocks.contains(next))
							continue;
						
						current = pos;
						dCurrent = dPos;
					}
				}
				
				template.sortedBlocks.add(current);
				template.remainingBlocks.remove(current);
				template.lastAddedBlock = current;
			}
			
			// update progress
			template.progress = (float)template.remainingBlocks.size()
				/ (float)template.totalBlocks;
			
			// continue with next step
			if(template.sortedBlocks.size() == template.totalBlocks)
			{
				step = Step.values()[step.ordinal() + 1];
				mc.displayGuiScreen(new GuiChooseName());
			}
		}
	}
	
	@Override
	public void onRender(float partialTicks)
	{
		// scale and offset
		double scale = 7D / 8D;
		double offset = (1D - scale) / 2D;
		
		// GL settings
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glLineWidth(2F);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		GL11.glPushMatrix();
		GL11.glTranslated(-mc.getRenderManager().renderPosX,
			-mc.getRenderManager().renderPosY,
			-mc.getRenderManager().renderPosZ);
		
		// area
		if(area != null)
		{
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			
			// recently scanned blocks
			if(step == Step.SCAN_AREA && area.progress < 1)
				for(int i = Math.max(0, area.blocksFound.size()
					- area.scanSpeed); i < area.blocksFound.size(); i++)
				{
					BlockPos pos = area.blocksFound.get(i);
					
					GL11.glPushMatrix();
					GL11.glTranslated(pos.getX(), pos.getY(), pos.getZ());
					GL11.glTranslated(-0.005, -0.005, -0.005);
					GL11.glScaled(1.01, 1.01, 1.01);
					
					GL11.glColor4f(0F, 1F, 0F, 0.15F);
					RenderUtils.drawSolidBox();
					
					GL11.glColor4f(0F, 0F, 0F, 0.5F);
					RenderUtils.drawOutlinedBox();
					
					GL11.glPopMatrix();
				}
			
			GL11.glPushMatrix();
			GL11.glTranslated(area.minX + offset, area.minY + offset,
				area.minZ + offset);
			GL11.glScaled(area.sizeX + scale, area.sizeY + scale,
				area.sizeZ + scale);
			
			// area scanner
			if(area.progress < 1)
			{
				GL11.glPushMatrix();
				GL11.glTranslated(0, 0, area.progress);
				GL11.glScaled(1, 1, 0);
				
				GL11.glColor4f(0F, 1F, 0F, 0.3F);
				RenderUtils.drawSolidBox();
				
				GL11.glColor4f(0F, 0F, 0F, 0.5F);
				RenderUtils.drawOutlinedBox();
				
				GL11.glPopMatrix();
				
				// template scanner
			}else if(template != null && template.progress > 0)
			{
				GL11.glPushMatrix();
				GL11.glTranslated(0, 0, template.progress);
				GL11.glScaled(1, 1, 0);
				
				GL11.glColor4f(0F, 1F, 0F, 0.3F);
				RenderUtils.drawSolidBox();
				
				GL11.glColor4f(0F, 0F, 0F, 0.5F);
				RenderUtils.drawOutlinedBox();
				
				GL11.glPopMatrix();
			}
			
			// area box
			GL11.glColor4f(0F, 0F, 0F, 0.5F);
			RenderUtils.drawOutlinedBox();
			
			GL11.glPopMatrix();
			
			GL11.glDisable(GL11.GL_DEPTH_TEST);
		}
		
		// sorted blocks
		if(template != null)
			for(BlockPos pos : template.sortedBlocks)
			{
				GL11.glPushMatrix();
				GL11.glTranslated(pos.getX(), pos.getY(), pos.getZ());
				GL11.glTranslated(offset, offset, offset);
				GL11.glScaled(scale, scale, scale);
				
				GL11.glColor4f(0F, 0F, 0F, 0.5F);
				RenderUtils.drawOutlinedBox();
				
				GL11.glPopMatrix();
			}
		
		// selected positions
		for(Step step : Step.SELECT_POSITION_STEPS)
		{
			BlockPos pos = step.pos;
			if(pos == null)
				continue;
			
			GL11.glPushMatrix();
			GL11.glTranslated(pos.getX(), pos.getY(), pos.getZ());
			GL11.glTranslated(offset, offset, offset);
			GL11.glScaled(scale, scale, scale);
			
			GL11.glColor4f(0F, 1F, 0F, 0.15F);
			RenderUtils.drawSolidBox();
			
			GL11.glColor4f(0F, 0F, 0F, 0.5F);
			RenderUtils.drawOutlinedBox();
			
			GL11.glPopMatrix();
		}
		
		// posLookingAt
		if(posLookingAt != null)
		{
			GL11.glPushMatrix();
			GL11.glTranslated(posLookingAt.getX(), posLookingAt.getY(),
				posLookingAt.getZ());
			GL11.glTranslated(offset, offset, offset);
			GL11.glScaled(scale, scale, scale);
			
			GL11.glColor4f(0.25F, 0.25F, 0.25F, 0.15F);
			RenderUtils.drawSolidBox();
			
			GL11.glColor4f(0F, 0F, 0F, 0.5F);
			RenderUtils.drawOutlinedBox();
			
			GL11.glPopMatrix();
		}
		
		GL11.glPopMatrix();
		
		// GL resets
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
	}
	
	@Override
	public void onRenderGUI()
	{
		// GL settings
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_CULL_FACE);
		
		GL11.glPushMatrix();
		
		String message;
		if(step.selectPos && step.pos != null)
			message = "Press enter to confirm, or select a different position.";
		else if(step == Step.FILE_NAME && file != null && file.exists())
			message = "WARNING: This file already exists.";
		else
			message = step.message;
		
		// translate to center
		ScaledResolution sr = new ScaledResolution(mc);
		int msgWidth = Fonts.segoe15.getStringWidth(message);
		GL11.glTranslated(sr.getScaledWidth() / 2 - msgWidth / 2,
			sr.getScaledHeight() / 2 + 1, 0);
		
		// background
		GL11.glColor4f(0, 0, 0, 0.5F);
		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glVertex2d(0, 0);
			GL11.glVertex2d(msgWidth + 2, 0);
			GL11.glVertex2d(msgWidth + 2, 10);
			GL11.glVertex2d(0, 10);
		}
		GL11.glEnd();
		
		// text
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		Fonts.segoe15.drawString(message, 2, -1, 0xffffffff);
		
		GL11.glPopMatrix();
		
		// GL resets
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	private void saveFile()
	{
		step = Step.values()[step.ordinal() + 1];
		
		new Thread(() -> {
			
			JsonObject json = new JsonObject();
			
			// get facings
			EnumFacing front = EnumFacing.getHorizontal(4 - WMinecraft
				.getPlayer().getHorizontalFacing().getHorizontalIndex());
			EnumFacing left = front.rotateYCCW();
			
			// add sorted blocks
			JsonArray jsonBlocks = new JsonArray();
			for(BlockPos pos : template.sortedBlocks)
			{
				// translate
				pos = pos.subtract(Step.FIRST_BLOCK.pos);
				
				// rotate
				pos = new BlockPos(0, pos.getY(), 0).offset(front, pos.getZ())
					.offset(left, pos.getX());
				
				// add to json
				jsonBlocks.add(JsonUtils.gson.toJsonTree(
					new int[]{pos.getX(), pos.getY(), pos.getZ()},
					int[].class));
			}
			json.add("blocks", jsonBlocks);
			
			try(PrintWriter save = new PrintWriter(new FileWriter(file)))
			{
				// save file
				save.print(JsonUtils.prettyGson.toJson(json));
				
				// show success message
				TextComponentString message =
					new TextComponentString("Saved template as ");
				TextComponentString link =
					new TextComponentString(file.getName());
				ClickEvent event = new ClickEvent(ClickEvent.Action.OPEN_FILE,
					file.getParentFile().getAbsolutePath());
				link.getStyle().setUnderlined(true).setClickEvent(event);
				message.appendSibling(link);
				ChatUtils.component(message);
				
			}catch(IOException e)
			{
				e.printStackTrace();
				
				// show error message
				ChatUtils.error("File could not be saved.");
			}
			
			// update AutoBuild
			wurst.mods.autoBuildMod.loadTemplates();
			
			// disable TemplateTool
			setEnabled(false);
			
		}, "TemplateTool").start();
	}
	
	private static enum Step
	{
		START_POS("Select start position.", true),
		
		END_POS("Select end position.", true),
		
		SCAN_AREA("Scanning area...", false),
		
		FIRST_BLOCK("Select the first block to be placed by AutoBuild.", true),
		
		CREATE_TEMPLATE("Creating template...", false),
		
		FILE_NAME("Choose a name for this template.", false),
		
		SAVE_FILE("Saving file...", false);
		
		private static final Step[] SELECT_POSITION_STEPS =
			{START_POS, END_POS, FIRST_BLOCK};
		
		private final String message;
		private boolean selectPos;
		
		private BlockPos pos;
		
		private Step(String message, boolean selectPos)
		{
			this.message = message;
			this.selectPos = selectPos;
		}
	}
	
	private static class Area
	{
		private final int minX, minY, minZ;
		private final int sizeX, sizeY, sizeZ;
		
		private final int totalBlocks, scanSpeed;
		private final Iterator<BlockPos> iterator;
		
		private int scannedBlocks;
		private float progress;
		
		private final ArrayList<BlockPos> blocksFound = new ArrayList<>();
		
		private Area(BlockPos start, BlockPos end)
		{
			int startX = start.getX();
			int startY = start.getY();
			int startZ = start.getZ();
			
			int endX = end.getX();
			int endY = end.getY();
			int endZ = end.getZ();
			
			minX = Math.min(startX, endX);
			minY = Math.min(startY, endY);
			minZ = Math.min(startZ, endZ);
			
			sizeX = Math.abs(startX - endX);
			sizeY = Math.abs(startY - endY);
			sizeZ = Math.abs(startZ - endZ);
			
			totalBlocks = (sizeX + 1) * (sizeY + 1) * (sizeZ + 1);
			scanSpeed = WMath.clamp(totalBlocks / 30, 1, 1024);
			iterator = BlockPos.getAllInBox(start, end).iterator();
		}
	}
	
	private static class Template
	{
		private final int totalBlocks, scanSpeed;
		private float progress;
		
		private final TreeSet<BlockPos> remainingBlocks;
		private final LinkedHashSet<BlockPos> sortedBlocks =
			new LinkedHashSet<>();
		private BlockPos lastAddedBlock;
		
		public Template(BlockPos firstBlock, int blocksFound)
		{
			totalBlocks = blocksFound;
			scanSpeed = WMath.clamp(blocksFound / 15, 1, 1024);
			
			remainingBlocks = new TreeSet<>((o1, o2) -> {
				
				// compare distance to start pos
				int distanceDiff = Double.compare(o1.distanceSq(firstBlock),
					o2.distanceSq(firstBlock));
				if(distanceDiff != 0)
					return distanceDiff;
				else
					return o1.compareTo(o2);
			});
		}
	}
	
	private static class GuiChooseName extends GuiScreen
	{
		private final GuiTextField nameField =
			new GuiTextField(0, Fonts.segoe15, 0, 0, 198, 16);
		private final GuiButton doneButton =
			new GuiButton(0, 0, 0, 150, 20, "Done");
		private final GuiButton cancelButton =
			new GuiButton(1, 0, 0, 100, 15, "Cancel");
		
		@Override
		public void initGui()
		{
			// middle
			int middleX = width / 2;
			int middleY = height / 2;
			
			// name field
			nameField.xPosition = middleX - 99;
			nameField.yPosition = middleY + 16;
			nameField.setEnableBackgroundDrawing(false);
			nameField.setMaxStringLength(32);
			nameField.setFocused(true);
			nameField.setTextColor(0xffffff);
			
			// done button
			doneButton.xPosition = middleX - 75;
			doneButton.yPosition = middleY + 38;
			buttonList.add(doneButton);
			
			// cancel button
			cancelButton.xPosition = middleX - 50;
			cancelButton.yPosition = middleY + 62;
			buttonList.add(cancelButton);
		}
		
		@Override
		protected void actionPerformed(GuiButton button) throws IOException
		{
			switch(button.id)
			{
				case 0:
				if(nameField.getText().isEmpty()
					|| wurst.mods.templateToolMod.file == null)
					return;
				
				mc.displayGuiScreen(null);
				wurst.mods.templateToolMod.saveFile();
				
				break;
				case 1:
				mc.displayGuiScreen(null);
				wurst.mods.templateToolMod.setEnabled(false);
				break;
			}
		}
		
		@Override
		public void updateScreen()
		{
			nameField.updateCursorCounter();
			
			if(!nameField.getText().isEmpty())
				wurst.mods.templateToolMod.file =
					new File(WurstFolders.AUTOBUILD.toFile(),
						nameField.getText() + ".json");
		}
		
		@Override
		protected void keyTyped(char typedChar, int keyCode) throws IOException
		{
			if(keyCode == 1)
			{
				actionPerformed(cancelButton);
				return;
			}
			
			if(keyCode == 28)
			{
				actionPerformed(doneButton);
				return;
			}
			
			nameField.textboxKeyTyped(typedChar, keyCode);
		}
		
		@Override
		public void drawScreen(int mouseX, int mouseY, float partialTicks)
		{
			// middle
			int middleX = width / 2;
			int middleY = height / 2;
			
			// background positions
			int x1 = middleX - 100;
			int y1 = middleY + 15;
			int x2 = middleX + 100;
			int y2 = middleY + 26;
			
			// background
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glColor4f(0, 0, 0, 0.5F);
			GL11.glBegin(GL11.GL_QUADS);
			{
				GL11.glVertex2d(x1, y1);
				GL11.glVertex2d(x2, y1);
				GL11.glVertex2d(x2, y2);
				GL11.glVertex2d(x1, y2);
			}
			GL11.glEnd();
			GL11.glColor4f(1, 1, 1, 1);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_CULL_FACE);
			
			// name field
			nameField.drawTextBox();
			
			// buttons
			super.drawScreen(mouseX, mouseY, partialTicks);
		}
		
		@Override
		public boolean doesGuiPauseGame()
		{
			return false;
		}
	}
}
