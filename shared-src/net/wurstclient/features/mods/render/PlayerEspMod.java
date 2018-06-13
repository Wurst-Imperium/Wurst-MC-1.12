/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.render;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.lwjgl.opengl.GL11;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.CameraTransformViewBobbingListener;
import net.wurstclient.events.RenderListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Feature;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.utils.EntityFakePlayer;
import net.wurstclient.utils.RenderUtils;
import net.wurstclient.utils.RotationUtils;

@SearchTags({"player esp", "PlayerTracers", "player tracers"})
@Mod.Bypasses
public final class PlayerEspMod extends Mod implements UpdateListener,
	CameraTransformViewBobbingListener, RenderListener
{
	private final CheckboxSetting tracers =
		new CheckboxSetting("Tracers", "Draws lines to players.", true);
	
	private int playerBox;
	private final ArrayList<EntityPlayer> players = new ArrayList<>();
	
	public PlayerEspMod()
	{
		super("PlayerESP",
			"Highlights nearby players.\n"
				+ "ESP boxes of friends will appear in blue.\n"
				+ "Sleeping or invisible players will not be\n"
				+ "shown if they are disabled in Target.");
		setCategory(Category.RENDER);
		addSetting(tracers);
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.mods.mobEspMod, wurst.mods.itemEspMod,
			wurst.mods.chestEspMod, wurst.mods.playerFinderMod,
			wurst.special.targetSpf};
	}
	
	@Override
	public void onEnable()
	{
		wurst.events.add(UpdateListener.class, this);
		wurst.events.add(CameraTransformViewBobbingListener.class, this);
		wurst.events.add(RenderListener.class, this);
		
		playerBox = GL11.glGenLists(1);
		GL11.glNewList(playerBox, GL11.GL_COMPILE);
		AxisAlignedBB bb = new AxisAlignedBB(-0.5, 0, -0.5, 0.5, 1, 0.5);
		RenderUtils.drawOutlinedBox(bb);
		GL11.glEndList();
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
		wurst.events.remove(CameraTransformViewBobbingListener.class, this);
		wurst.events.remove(RenderListener.class, this);
		
		GL11.glDeleteLists(playerBox, 1);
		playerBox = 0;
	}
	
	@Override
	public void onUpdate()
	{
		EntityPlayer player = WMinecraft.getPlayer();
		World world = WMinecraft.getWorld();
		
		players.clear();
		Stream<EntityPlayer> stream = world.playerEntities.parallelStream()
			.filter(e -> !e.isDead && e.getHealth() > 0)
			.filter(e -> e != player)
			.filter(e -> !(e instanceof EntityFakePlayer))
			.filter(e -> Math.abs(e.posY - WMinecraft.getPlayer().posY) <= 1e6);
		
		if(!wurst.special.targetSpf.sleepingPlayers.isChecked())
			stream = stream.filter(e -> !e.isPlayerSleeping());
		
		if(!wurst.special.targetSpf.invisiblePlayers.isChecked())
			stream = stream.filter(e -> !e.isInvisible());
		
		players.addAll(stream.collect(Collectors.toList()));
	}
	
	@Override
	public void onCameraTransformViewBobbing(
		CameraTransformViewBobbingEvent event)
	{
		if(tracers.isChecked())
			event.cancel();
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
		renderBoxes(partialTicks);
		
		if(tracers.isChecked())
			renderTracers(partialTicks);
		
		GL11.glPopMatrix();
		
		// GL resets
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
	}
	
	private void renderBoxes(double partialTicks)
	{
		for(EntityPlayer e : players)
		{
			// set position
			GL11.glPushMatrix();
			GL11.glTranslated(e.prevPosX + (e.posX - e.prevPosX) * partialTicks,
				e.prevPosY + (e.posY - e.prevPosY) * partialTicks,
				e.prevPosZ + (e.posZ - e.prevPosZ) * partialTicks);
			GL11.glScaled(e.width + 0.1, e.height + 0.1, e.width + 0.1);
			
			// set color
			if(wurst.friends.contains(e.getName()))
				GL11.glColor4f(0, 0, 1, 0.5F);
			else
			{
				float f = WMinecraft.getPlayer().getDistanceToEntity(e) / 20F;
				GL11.glColor4f(2 - f, f, 0, 0.5F);
			}
			
			// draw box
			GL11.glCallList(playerBox);
			
			GL11.glPopMatrix();
		}
	}
	
	private void renderTracers(double partialTicks)
	{
		Vec3d start = RotationUtils.getClientLookVec()
			.addVector(0, WMinecraft.getPlayer().getEyeHeight(), 0)
			.addVector(mc.getRenderManager().renderPosX,
				mc.getRenderManager().renderPosY,
				mc.getRenderManager().renderPosZ);
		
		GL11.glBegin(GL11.GL_LINES);
		for(EntityPlayer e : players)
		{
			Vec3d end = e.getEntityBoundingBox().getCenter()
				.subtract(new Vec3d(e.posX, e.posY, e.posZ)
					.subtract(e.prevPosX, e.prevPosY, e.prevPosZ)
					.scale(1 - partialTicks));
			
			if(wurst.friends.contains(e.getName()))
				GL11.glColor4f(0, 0, 1, 0.5F);
			else
			{
				float f = WMinecraft.getPlayer().getDistanceToEntity(e) / 20F;
				GL11.glColor4f(2 - f, f, 0, 0.5F);
			}
			
			GL11.glVertex3d(start.xCoord, start.yCoord, start.zCoord);
			GL11.glVertex3d(end.xCoord, end.yCoord, end.zCoord);
		}
		GL11.glEnd();
	}
}
