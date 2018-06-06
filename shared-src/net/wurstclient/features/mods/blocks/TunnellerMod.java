/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.blocks;

import org.lwjgl.opengl.GL11;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.wurstclient.compatibility.WBlock;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.PostUpdateListener;
import net.wurstclient.events.RenderListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Feature;
import net.wurstclient.features.Mod;
import net.wurstclient.features.special_features.YesCheatSpf.Profile;
import net.wurstclient.settings.ModeSetting;
import net.wurstclient.utils.BlockUtils;
import net.wurstclient.utils.RenderUtils;
import net.wurstclient.utils.RotationUtils;

@Mod.Bypasses
public final class TunnellerMod extends Mod
	implements UpdateListener, PostUpdateListener, RenderListener
{
	private final ModeSetting mode =
		new ModeSetting("Mode", new String[]{"Fast", "Legit"}, 1);
	private BlockPos currentBlock;
	
	public TunnellerMod()
	{
		super("Tunneller", "Digs a 3x3 tunnel around you.");
		setCategory(Category.BLOCKS);
	}
	
	@Override
	public void initSettings()
	{
		addSetting(mode);
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.mods.nukerMod, wurst.mods.nukerLegitMod,
			wurst.mods.speedNukerMod};
	}
	
	@Override
	public void onEnable()
	{
		// disable other nukers
		wurst.mods.nukerMod.setEnabled(false);
		wurst.mods.nukerLegitMod.setEnabled(false);
		wurst.mods.speedNukerMod.setEnabled(false);
		
		// add listeners
		wurst.events.add(UpdateListener.class, this);
		wurst.events.add(PostUpdateListener.class, this);
		wurst.events.add(RenderListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		// remove listeners
		wurst.events.remove(UpdateListener.class, this);
		wurst.events.remove(PostUpdateListener.class, this);
		wurst.events.remove(RenderListener.class, this);
		
		// resets
		mc.playerController.resetBlockRemoving();
		currentBlock = null;
	}
	
	@Override
	public void onUpdate()
	{
		boolean legit = mode.getSelected() == 1;
		
		currentBlock = null;
		
		// get valid blocks
		Iterable<BlockPos> validBlocks =
			BlockUtils.getValidBlocks(1, (p) -> true);
		
		// nuke all
		if(WMinecraft.getPlayer().capabilities.isCreativeMode && !legit)
		{
			mc.playerController.resetBlockRemoving();
			
			// prepare distance check
			Vec3d eyesPos = RotationUtils.getEyesPos().subtract(0.5, 0.5, 0.5);
			double closestDistanceSq = Double.POSITIVE_INFINITY;
			
			// break all blocks
			for(BlockPos pos : validBlocks)
			{
				BlockUtils.breakBlockPacketSpam(pos);
				
				// find closest block
				double currentDistanceSq =
					eyesPos.squareDistanceTo(new Vec3d(pos));
				if(currentDistanceSq < closestDistanceSq)
				{
					closestDistanceSq = currentDistanceSq;
					currentBlock = pos;
				}
			}
			
			return;
		}
		
		// find valid block
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
	
	@Override
	public void afterUpdate()
	{
		boolean legit = mode.getSelected() == 1;
		
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
			case OFF:
			case MINEPLEX:
			mode.unlock();
			break;
			
			default:
			mode.lock(1);
			break;
		}
	}
}
