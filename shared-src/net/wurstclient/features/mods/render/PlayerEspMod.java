/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.listeners.RenderListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Feature;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;
import net.wurstclient.utils.EntityFakePlayer;
import net.wurstclient.utils.RenderUtils;

@SearchTags({"player esp"})
@Mod.Bypasses
public final class PlayerEspMod extends Mod implements RenderListener
{
	private static final AxisAlignedBB PLAYER_BOX =
		new AxisAlignedBB(-0.35, 0, -0.35, 0.35, 1.9, 0.35);
	
	public PlayerEspMod()
	{
		super("PlayerESP", "Allows you to see players through walls.");
		setCategory(Category.RENDER);
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.mods.tracersMod, wurst.mods.mobEspMod,
			wurst.mods.itemEspMod};
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
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		GL11.glPushMatrix();
		GL11.glTranslated(-mc.getRenderManager().renderPosX,
			-mc.getRenderManager().renderPosY,
			-mc.getRenderManager().renderPosZ);
		
		// draw boxes
		for(EntityPlayer entity : WMinecraft.getWorld().playerEntities)
		{
			if(entity == WMinecraft.getPlayer()
				|| entity instanceof EntityFakePlayer)
				continue;
			
			if(!wurst.special.targetSpf.sleepingPlayers.isChecked()
				&& entity.isPlayerSleeping())
				continue;
			
			if(!wurst.special.targetSpf.invisiblePlayers.isChecked()
				&& entity.isInvisible())
				continue;
			
			// set color
			if(wurst.friends.contains(entity.getName()))
				GL11.glColor4f(0, 0, 1, 0.5F);
			else
			{
				float factor =
					WMinecraft.getPlayer().getDistanceToEntity(entity) / 20F;
				if(factor > 2)
					factor = 2;
				GL11.glColor4f(2 - factor, factor, 0, 0.5F);
			}
			
			// set position
			GL11.glPushMatrix();
			GL11.glTranslated(
				entity.prevPosX
					+ (entity.posX - entity.prevPosX) * partialTicks,
				entity.prevPosY
					+ (entity.posY - entity.prevPosY) * partialTicks,
				entity.prevPosZ
					+ (entity.posZ - entity.prevPosZ) * partialTicks);
			
			// draw box
			RenderUtils.drawOutlinedBox(PLAYER_BOX);
			
			GL11.glPopMatrix();
		}
		
		GL11.glPopMatrix();
		
		// GL resets
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
	}
}
