/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.blocks;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.compatibility.WBlock;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.LeftClickListener;
import net.wurstclient.events.RenderListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Feature;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.ModeSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;
import net.wurstclient.utils.BlockUtils;
import net.wurstclient.utils.BlockUtils.BlockValidator;
import net.wurstclient.utils.RenderUtils;

@SearchTags({"LegitNuker", "nuker legit", "legit nuker"})
@Mod.Bypasses
public final class NukerLegitMod extends Mod
	implements LeftClickListener, RenderListener, UpdateListener
{
	private final CheckboxSetting useNuker =
		new CheckboxSetting("Use Nuker settings", true)
		{
			@Override
			public void update()
			{
				if(isChecked())
				{
					NukerMod nuker = wurst.mods.nukerMod;
					range.lock(nuker.range);
					mode.lock(nuker.mode.getSelected());
				}else
				{
					range.unlock();
					mode.unlock();
				}
			}
		};
	private final SliderSetting range =
		new SliderSetting("Range", 4.25, 1, 4.25, 0.05, ValueDisplay.DECIMAL);
	private final ModeSetting mode = new ModeSetting("Mode",
		new String[]{"Normal", "ID", "Flat", "Smash"}, 0)
	{
		@Override
		public void update()
		{
			switch(getSelected())
			{
				default:
				case 0:
				// normal mode
				validator = (pos) -> true;
				break;
				
				case 1:
				// id mode
				validator =
					(pos) -> wurst.mods.nukerMod.getId() == WBlock.getId(pos);
				break;
				
				case 2:
				// flat mode
				validator = (pos) -> pos.getY() >= WMinecraft.getPlayer().posY;
				break;
				
				case 3:
				// smash mode
				validator = (pos) -> WBlock.getHardness(pos) >= 1;
				break;
			}
		}
	};
	
	private BlockPos currentBlock;
	private BlockValidator validator;
	
	public NukerLegitMod()
	{
		super("NukerLegit",
			"Slower Nuker that bypasses all AntiCheat plugins.\n"
				+ "Not required on normal NoCheat+ servers!");
		setCategory(Category.BLOCKS);
	}
	
	@Override
	public void initSettings()
	{
		addSetting(useNuker);
		addSetting(range);
		addSetting(mode);
	}
	
	@Override
	public String getRenderName()
	{
		switch(mode.getSelected())
		{
			case 0:
			return "NukerLegit";
			
			case 1:
			return "IDNukerLegit [" + wurst.mods.nukerMod.getId() + "]";
			
			default:
			return mode.getSelectedMode() + "NukerLegit";
		}
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.mods.nukerMod, wurst.mods.speedNukerMod,
			wurst.mods.tunnellerMod};
	}
	
	@Override
	public void onEnable()
	{
		// disable other nukers
		wurst.mods.nukerMod.setEnabled(false);
		wurst.mods.speedNukerMod.setEnabled(false);
		wurst.mods.tunnellerMod.setEnabled(false);
		
		// add listeners
		wurst.events.add(LeftClickListener.class, this);
		wurst.events.add(UpdateListener.class, this);
		wurst.events.add(RenderListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		// remove listeners
		wurst.events.remove(LeftClickListener.class, this);
		wurst.events.remove(UpdateListener.class, this);
		wurst.events.remove(RenderListener.class, this);
		
		// resets
		mc.gameSettings.keyBindAttack.pressed = false;
		currentBlock = null;
		wurst.mods.nukerMod.setId(0);
	}
	
	@Override
	public void onLeftClick(LeftClickEvent event)
	{
		// check hitResult
		if(mc.objectMouseOver == null
			|| mc.objectMouseOver.getBlockPos() == null)
			return;
		
		// check mode
		if(mode.getSelected() != 1)
			return;
		
		// check material
		if(WBlock.getMaterial(mc.objectMouseOver.getBlockPos()) == Material.AIR)
			return;
		
		// set id
		wurst.mods.nukerMod
			.setId(WBlock.getId(mc.objectMouseOver.getBlockPos()));
	}
	
	@Override
	public void onUpdate()
	{
		// abort if using IDNuker without an ID being set
		if(mode.getSelected() == 1 && wurst.mods.nukerMod.getId() == 0)
			return;
		
		currentBlock = null;
		
		// get valid blocks
		Iterable<BlockPos> validBlocks = BlockUtils
			.getValidBlocksByDistance(range.getValue(), false, validator);
		
		// find closest valid block
		for(BlockPos pos : validBlocks)
		{
			// break block
			if(!BlockUtils.breakBlockExtraLegit(pos))
				continue;
			
			// set currentBlock if successful
			currentBlock = pos;
			break;
		}
		
		// reset if no block was found
		if(currentBlock == null)
			mc.gameSettings.keyBindAttack.pressed = false;
	}
	
	@Override
	public void onRender(float partialTicks)
	{
		if(currentBlock == null)
			return;
		
		// GL settings
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glLineWidth(2);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		GL11.glPushMatrix();
		GL11.glTranslated(-mc.getRenderManager().renderPosX,
			-mc.getRenderManager().renderPosY,
			-mc.getRenderManager().renderPosZ);
		
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
		
		GL11.glPopMatrix();
		
		// GL resets
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
	}
}
