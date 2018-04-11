/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketEffect;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnGlobalEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.wurstclient.compatibility.WMath;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.PacketInputListener;
import net.wurstclient.events.RenderListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Feature;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;
import net.wurstclient.utils.RenderUtils;
import net.wurstclient.utils.RotationUtils;

@SearchTags({"player finder"})
@Mod.Bypasses
public final class PlayerFinderMod extends Mod
	implements PacketInputListener, RenderListener
{
	private BlockPos pos;
	
	public PlayerFinderMod()
	{
		super("PlayerFinder", "Finds far players during thunderstorms.");
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
		pos = null;
		
		wurst.events.add(PacketInputListener.class, this);
		wurst.events.add(RenderListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(PacketInputListener.class, this);
		wurst.events.remove(RenderListener.class, this);
	}
	
	@Override
	public void onRender(float partialTicks)
	{
		if(pos == null)
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
		
		// generate rainbow color
		float x = System.currentTimeMillis() % 2000 / 1000F;
		float red = 0.5F + 0.5F * WMath.sin(x * (float)Math.PI);
		float green = 0.5F + 0.5F * WMath.sin((x + 4F / 3F) * (float)Math.PI);
		float blue = 0.5F + 0.5F * WMath.sin((x + 8F / 3F) * (float)Math.PI);
		
		GL11.glColor4f(red, green, blue, 0.5F);
		
		// tracer line
		GL11.glBegin(GL11.GL_LINES);
		{
			// set start position
			Vec3d start = RotationUtils.getClientLookVec()
				.addVector(0, WMinecraft.getPlayer().getEyeHeight(), 0)
				.addVector(mc.getRenderManager().renderPosX,
					mc.getRenderManager().renderPosY,
					mc.getRenderManager().renderPosZ);
			
			// set end position
			Vec3d end = new Vec3d(pos).addVector(0.5, 0.5, 0.5);
			
			// draw line
			GL11.glVertex3d(start.xCoord, start.yCoord, start.zCoord);
			GL11.glVertex3d(end.xCoord, end.yCoord, end.zCoord);
		}
		GL11.glEnd();
		
		// block box
		{
			GL11.glPushMatrix();
			GL11.glTranslated(pos.getX(), pos.getY(), pos.getZ());
			
			RenderUtils.drawOutlinedBox();
			
			GL11.glColor4f(red, green, blue, 0.25F);
			RenderUtils.drawSolidBox();
			
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
	public void onReceivedPacket(PacketInputEvent event)
	{
		if(WMinecraft.getPlayer() == null)
			return;
		
		Packet packet = event.getPacket();
		
		// get packet position
		BlockPos newPos = null;
		if(packet instanceof SPacketEffect)
		{
			SPacketEffect effect = (SPacketEffect)packet;
			newPos = effect.getSoundPos();
			
		}else if(packet instanceof SPacketSoundEffect)
		{
			SPacketSoundEffect sound = (SPacketSoundEffect)packet;
			newPos = new BlockPos(sound.getX(), sound.getY(), sound.getZ());
			
		}else if(packet instanceof SPacketSpawnGlobalEntity)
		{
			SPacketSpawnGlobalEntity lightning =
				(SPacketSpawnGlobalEntity)packet;
			newPos = new BlockPos(lightning.getX() / 32D,
				lightning.getY() / 32D, lightning.getZ() / 32D);
		}
		
		if(newPos == null)
			return;
		
		// check distance to player
		BlockPos playerPos = new BlockPos(WMinecraft.getPlayer());
		if(Math.abs(playerPos.getX() - newPos.getX()) > 250
			|| Math.abs(playerPos.getZ() - newPos.getZ()) > 250)
			pos = newPos;
	}
}
