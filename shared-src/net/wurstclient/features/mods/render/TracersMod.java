/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.listeners.RenderListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Feature;
import net.wurstclient.features.Mod;
import net.wurstclient.utils.EntityFakePlayer;
import net.wurstclient.utils.RotationUtils;

@Mod.Bypasses
public final class TracersMod extends Mod implements RenderListener
{
	public TracersMod()
	{
		super("Tracers", "Draws lines to players around you.");
		setCategory(Category.RENDER);
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.mods.playerEspMod};
	}
	
	@Override
	public void onEnable()
	{
		wurst.events.add(RenderListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(RenderListener.class, this);
	}
	
	@Override
	public void onRender(float partialTicks)
	{
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
		
		// set start position
		Vec3d start = RotationUtils.getClientLookVec()
			.addVector(0, WMinecraft.getPlayer().getEyeHeight(), 0)
			.addVector(mc.getRenderManager().renderPosX,
				mc.getRenderManager().renderPosY,
				mc.getRenderManager().renderPosZ);
		
		GL11.glBegin(GL11.GL_LINES);
		for(EntityPlayer entity : WMinecraft.getWorld().playerEntities)
		{
			if(entity == WMinecraft.getPlayer()
				|| entity instanceof EntityFakePlayer)
				continue;
			
			// fake entities with crazy vertical position
			if(Math.abs(entity.posY - WMinecraft.getPlayer().posY) > 1e6)
				continue;
			
			if(!wurst.special.targetSpf.sleepingPlayers.isChecked()
				&& entity.isPlayerSleeping())
				continue;
			
			if(!wurst.special.targetSpf.invisiblePlayers.isChecked()
				&& entity.isInvisible())
				continue;
			
			// set end position
			Vec3d end = entity.boundingBox.getCenter()
				.subtract(new Vec3d(entity.posX, entity.posY, entity.posZ)
					.subtract(entity.prevPosX, entity.prevPosY, entity.prevPosZ)
					.scale(1 - partialTicks));
			
			// set color
			if(wurst.friends.contains(entity.getName()))
				GL11.glColor4f(0, 0, 1, 0.5F);
			else
			{
				float factor =
					WMinecraft.getPlayer().getDistanceToEntity(entity) / 40F;
				if(factor > 1)
					factor = 1;
				
				GL11.glColor4f(2 - factor * 2F, factor * 2F, 0, 0.5F);
			}
			
			// draw line
			GL11.glVertex3d(start.xCoord, start.yCoord, start.zCoord);
			GL11.glVertex3d(end.xCoord, end.yCoord, end.zCoord);
		}
		GL11.glEnd();
		
		GL11.glPopMatrix();
		
		// GL resets
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
	}
}
