/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
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
import net.wurstclient.events.LeftClickEvent;
import net.wurstclient.events.listeners.LeftClickListener;
import net.wurstclient.events.listeners.PostUpdateListener;
import net.wurstclient.events.listeners.RenderListener;
import net.wurstclient.events.listeners.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Feature;
import net.wurstclient.features.Mod;
import net.wurstclient.features.special_features.YesCheatSpf.Profile;
import net.wurstclient.settings.ModeSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;
import net.wurstclient.utils.BlockUtils;
import net.wurstclient.utils.BlockUtils.BlockValidator;
import net.wurstclient.utils.RenderUtils;

@Mod.Bypasses
public final class NukerMod extends Mod implements LeftClickListener,
	UpdateListener, PostUpdateListener, RenderListener
{
	public final SliderSetting range =
		new SliderSetting("Range", 6, 1, 6, 0.05, ValueDisplay.DECIMAL);
	public final ModeSetting mode = new ModeSetting("Mode",
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
				validator = (pos) -> id == WBlock.getId(pos);
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
	private final ModeSetting mode2 =
		new ModeSetting("Mode 2", new String[]{"Fast", "Legit"}, 0);
	
	public int id = 0;
	private BlockPos currentBlock;
	private BlockValidator validator;
	
	public NukerMod()
	{
		super("Nuker", "Destroys blocks around you.");
		setCategory(Category.BLOCKS);
	}
	
	@Override
	public void initSettings()
	{
		addSetting(range);
		addSetting(mode);
		addSetting(mode2);
	}
	
	@Override
	public String getRenderName()
	{
		switch(mode.getSelected())
		{
			case 0:
			return "Nuker";
			
			case 1:
			return "IDNuker [" + id + "]";
			
			default:
			return mode.getSelectedMode() + "Nuker";
		}
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.mods.nukerLegitMod, wurst.mods.speedNukerMod,
			wurst.mods.tunnellerMod};
	}
	
	@Override
	public void onEnable()
	{
		// disable other nukers
		wurst.mods.nukerLegitMod.setEnabled(false);
		wurst.mods.speedNukerMod.setEnabled(false);
		wurst.mods.tunnellerMod.setEnabled(false);
		
		// add listeners
		wurst.events.add(LeftClickListener.class, this);
		wurst.events.add(UpdateListener.class, this);
		wurst.events.add(PostUpdateListener.class, this);
		wurst.events.add(RenderListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		// remove listeners
		wurst.events.remove(LeftClickListener.class, this);
		wurst.events.remove(UpdateListener.class, this);
		wurst.events.remove(PostUpdateListener.class, this);
		wurst.events.remove(RenderListener.class, this);
		
		// resets
		mc.playerController.resetBlockRemoving();
		currentBlock = null;
		id = 0;
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
		id = WBlock.getId(mc.objectMouseOver.getBlockPos());
	}
	
	@Override
	public void onUpdate()
	{
		// abort if using IDNuker without an ID being set
		if(mode.getSelected() == 1 && id == 0)
			return;
		
		boolean legit = mode2.getSelected() == 1;
		
		currentBlock = null;
		
		// get valid blocks
		Iterable<BlockPos> validBlocks = BlockUtils
			.getValidBlocksByDistance(range.getValue(), !legit, validator);
		
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
			
			return;
		}
		
		// find closest valid block
		for(BlockPos pos : validBlocks)
		{
			boolean successful;
			
			// break block
			if(legit)
				successful = BlockUtils.prepareToBreakBlockLegit(pos);
			else
				successful = BlockUtils.breakBlockSimple(pos);
			
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
	
	@Override
	public void afterUpdate()
	{
		boolean legit = mode2.getSelected() == 1;
		
		// break block
		if(currentBlock != null && legit)
			BlockUtils.breakBlockLegit(currentBlock);
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
	
	@Override
	public void onYesCheatUpdate(Profile profile)
	{
		switch(profile)
		{
			default:
			case OFF:
			case MINEPLEX:
			range.resetUsableMax();
			mode2.unlock();
			break;
			
			case ANTICHEAT:
			case OLDER_NCP:
			case LATEST_NCP:
			case GHOST_MODE:
			range.setUsableMax(4.25);
			mode2.lock(1);
			break;
		}
	}
}
