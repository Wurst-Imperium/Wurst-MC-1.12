/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.blocks;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.function.Predicate;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.ai.PathFinder;
import net.wurstclient.ai.PathProcessor;
import net.wurstclient.compatibility.WBlock;
import net.wurstclient.compatibility.WMath;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.GUIRenderListener;
import net.wurstclient.events.PostUpdateListener;
import net.wurstclient.events.RenderListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Feature;
import net.wurstclient.features.Mod;
import net.wurstclient.features.commands.PathCmd;
import net.wurstclient.features.special_features.YesCheatSpf.Profile;
import net.wurstclient.font.Fonts;
import net.wurstclient.settings.ModeSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;
import net.wurstclient.utils.BlockUtils;
import net.wurstclient.utils.RenderUtils;

@Mod.Bypasses
public final class ExcavatorMod extends Mod implements UpdateListener,
	PostUpdateListener, RenderListener, GUIRenderListener
{
	private Step step;
	private BlockPos posLookingAt;
	private Area area;
	private BlockPos currentBlock;
	private ExcavatorPathFinder pathFinder;
	private PathProcessor processor;
	
	private final SliderSetting range =
		new SliderSetting("Range", 6, 2, 6, 0.05, ValueDisplay.DECIMAL);
	private final ModeSetting mode =
		new ModeSetting("Mode", new String[]{"Fast", "Legit"}, 0);
	
	public ExcavatorMod()
	{
		super("Excavator",
			"Automatically destroys all blocks in the selected area.");
		setCategory(Category.BLOCKS);
	}
	
	@Override
	public void initSettings()
	{
		addSetting(range);
		addSetting(mode);
	}
	
	@Override
	public String getRenderName()
	{
		String name = getName();
		
		if(step == Step.EXCAVATE && area != null)
			name += " "
				+ (int)((float)(area.blocksList.size() - area.remainingBlocks)
					/ (float)area.blocksList.size() * 100)
				+ "%";
		
		return name;
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.commands.excavateCmd, wurst.mods.nukerMod};
	}
	
	@Override
	public void onEnable()
	{
		// disable conflicting mods
		wurst.mods.bowAimbotMod.setEnabled(false);
		wurst.mods.templateToolMod.setEnabled(false);
		
		step = Step.START_POS;
		
		wurst.events.add(UpdateListener.class, this);
		wurst.events.add(PostUpdateListener.class, this);
		wurst.events.add(RenderListener.class, this);
		wurst.events.add(GUIRenderListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
		wurst.events.remove(PostUpdateListener.class, this);
		wurst.events.remove(RenderListener.class, this);
		wurst.events.remove(GUIRenderListener.class, this);
		
		for(Step step : Step.values())
			step.pos = null;
		posLookingAt = null;
		area = null;
		
		mc.playerController.resetBlockRemoving();
		currentBlock = null;
		
		pathFinder = null;
		processor = null;
		PathProcessor.releaseControls();
	}
	
	@Override
	public void onUpdate()
	{
		if(step.selectPos)
			handlePositionSelection();
		else if(step == Step.SCAN_AREA)
			scanArea();
		else if(step == Step.EXCAVATE)
			excavate();
	}
	
	@Override
	public void afterUpdate()
	{
		if(currentBlock != null && mode.getSelected() == 1)
			BlockUtils.breakBlockLegit(currentBlock);
	}
	
	@Override
	public void onRender(float partialTicks)
	{
		if(pathFinder != null)
		{
			PathCmd pathCmd = wurst.commands.pathCmd;
			pathFinder.renderPath(pathCmd.isDebugMode(), pathCmd.isDepthTest());
		}
		
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
				for(int i = Math.max(0, area.blocksList.size()
					- area.scanSpeed); i < area.blocksList.size(); i++)
				{
					BlockPos pos = area.blocksList.get(i);
					
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
			}
			
			// area box
			GL11.glColor4f(0F, 0F, 0F, 0.5F);
			RenderUtils.drawOutlinedBox();
			
			GL11.glPopMatrix();
			
			GL11.glDisable(GL11.GL_DEPTH_TEST);
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
		
		// currentBlock
		if(currentBlock != null)
		{
			// set position
			GL11.glTranslated(currentBlock.getX(), currentBlock.getY(),
				currentBlock.getZ());
			
			// get progress
			float progress;
			if(WBlock.getHardness(currentBlock) < 1)
				progress = mc.playerController.curBlockDamageMP;
			else
				progress = 1;
			
			// set size
			if(progress < 1)
			{
				GL11.glTranslated(0.5, 0.5, 0.5);
				GL11.glScaled(progress, progress, progress);
				GL11.glTranslated(-0.5, -0.5, -0.5);
			}
			
			// get color
			float red = progress * 2F;
			float green = 2 - red;
			
			// draw box
			GL11.glColor4f(red, green, 0, 0.25F);
			RenderUtils.drawSolidBox();
			GL11.glColor4f(red, green, 0, 0.5F);
			RenderUtils.drawOutlinedBox();
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
	
	@Override
	public void onYesCheatUpdate(Profile profile)
	{
		switch(profile)
		{
			default:
			case OFF:
			case MINEPLEX:
			range.resetUsableMax();
			mode.unlock();
			break;
			
			case ANTICHEAT:
			case OLDER_NCP:
			case LATEST_NCP:
			case GHOST_MODE:
			range.setUsableMax(4.25);
			mode.lock(1);
			break;
		}
	}
	
	public void enableWithArea(BlockPos pos1, BlockPos pos2)
	{
		setEnabled(true);
		Step.START_POS.pos = pos1;
		Step.END_POS.pos = pos2;
		step = Step.SCAN_AREA;
	}
	
	private void handlePositionSelection()
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
				posLookingAt = posLookingAt.offset(mc.objectMouseOver.sideHit);
			
		}else
			posLookingAt = null;
		
		// set selected position
		if(posLookingAt != null && mc.gameSettings.keyBindUseItem.pressed)
			step.pos = posLookingAt;
	}
	
	private void scanArea()
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
			{
				area.blocksList.add(pos);
				area.blocksSet.add(pos);
			}
		}
		
		// update progress
		area.progress = (float)area.scannedBlocks / (float)area.totalBlocks;
		
		// continue with next step
		if(!area.iterator.hasNext())
		{
			area.remainingBlocks = area.blocksList.size();
			step = Step.values()[step.ordinal() + 1];
		}
	}
	
	private void excavate()
	{
		boolean legit = mode.getSelected() == 1;
		currentBlock = null;
		
		// get valid blocks
		Iterable<BlockPos> validBlocks = BlockUtils.getValidBlocksByDistance(
			range.getValue(), !legit, pos -> area.blocksSet.contains(pos));
		
		// nuke all
		if(WMinecraft.getPlayer().capabilities.isCreativeMode && !legit)
		{
			mc.playerController.resetBlockRemoving();
			
			// set closest block as current
			for(BlockPos pos : validBlocks)
			{
				currentBlock = pos;
				break;
			}
			
			// break all blocks
			validBlocks.forEach((pos) -> BlockUtils.breakBlockPacketSpam(pos));
		}else
		{
			// find closest valid block
			for(BlockPos pos : validBlocks)
			{
				boolean successful;
				
				// break block
				if(legit)
					successful = BlockUtils.prepareToBreakBlockLegit(pos);
				else
					successful = BlockUtils.breakBlockSimple_old(pos);
				
				// set currentBlock if successful
				if(successful)
				{
					currentBlock = pos;
					break;
				}
			}
			
			// reset if no block was found
			if(currentBlock == null)
				mc.playerController.resetBlockRemoving();
		}
		
		// get remaining blocks
		Predicate<BlockPos> pClickable = pos -> WBlock.canBeClicked(pos);
		area.remainingBlocks =
			(int)area.blocksList.parallelStream().filter(pClickable).count();
		
		if(area.remainingBlocks == 0)
		{
			setEnabled(false);
			return;
		}
		
		if(pathFinder == null)
		{
			Comparator<BlockPos> cDistance = Comparator.comparingDouble(
				pos -> WMinecraft.getPlayer().getDistanceSqToCenter(pos));
			BlockPos closestBlock = area.blocksList.parallelStream()
				.filter(pClickable).min(cDistance).get();
			
			pathFinder = new ExcavatorPathFinder(closestBlock);
		}
		
		// find path
		if(!pathFinder.isDone() && !pathFinder.isFailed())
		{
			PathProcessor.lockControls();
			
			pathFinder.think();
			
			if(!pathFinder.isDone() && !pathFinder.isFailed())
				return;
			
			pathFinder.formatPath();
			
			// set processor
			processor = pathFinder.getProcessor();
		}
		
		// check path
		if(processor != null
			&& !pathFinder.isPathStillValid(processor.getIndex()))
		{
			pathFinder = new ExcavatorPathFinder(pathFinder);
			return;
		}
		
		// process path
		processor.process();
		
		if(processor.isDone())
		{
			pathFinder = null;
			processor = null;
			PathProcessor.releaseControls();
		}
	}
	
	private static enum Step
	{
		START_POS("Select start position.", true),
		
		END_POS("Select end position.", true),
		
		SCAN_AREA("Scanning area...", false),
		
		EXCAVATE("Excavating...", false);
		
		private static final Step[] SELECT_POSITION_STEPS =
			{START_POS, END_POS};
		
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
		
		private int scannedBlocks, remainingBlocks;
		private float progress;
		
		private final ArrayList<BlockPos> blocksList = new ArrayList<>();
		private final HashSet<BlockPos> blocksSet = new HashSet<>();
		
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
			scanSpeed = WMath.clamp(totalBlocks / 30, 1, 16384);
			iterator = BlockPos.getAllInBox(start, end).iterator();
		}
	}
	
	private static class ExcavatorPathFinder extends PathFinder
	{
		public ExcavatorPathFinder(BlockPos goal)
		{
			super(goal);
			setThinkTime(10);
		}
		
		public ExcavatorPathFinder(ExcavatorPathFinder pathFinder)
		{
			super(pathFinder);
		}
		
		@Override
		protected boolean checkDone()
		{
			BlockPos goal = getGoal();
			
			return done = goal.down(2).equals(current)
				|| goal.up().equals(current) || goal.north().equals(current)
				|| goal.south().equals(current) || goal.west().equals(current)
				|| goal.east().equals(current);
		}
	}
}
