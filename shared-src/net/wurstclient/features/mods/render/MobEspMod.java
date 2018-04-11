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

import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.CameraTransformViewBobbingListener;
import net.wurstclient.events.RenderListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Feature;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.utils.RenderUtils;
import net.wurstclient.utils.RotationUtils;

@SearchTags({"mob esp", "MobTracers", "mob tracers"})
@Mod.Bypasses
public final class MobEspMod extends Mod implements UpdateListener,
	CameraTransformViewBobbingListener, RenderListener
{
	private final CheckboxSetting tracers =
		new CheckboxSetting("Tracers", "Draws lines to mobs.", false);
	
	private int mobBox;
	private final ArrayList<EntityLiving> mobs = new ArrayList<>();
	
	public MobEspMod()
	{
		super("MobESP", "Highlights nearby mobs.");
		setCategory(Category.RENDER);
		addSetting(tracers);
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.mods.playerEspMod, wurst.mods.itemEspMod};
	}
	
	@Override
	public void onEnable()
	{
		wurst.events.add(UpdateListener.class, this);
		wurst.events.add(CameraTransformViewBobbingListener.class, this);
		wurst.events.add(RenderListener.class, this);
		
		mobBox = GL11.glGenLists(1);
		GL11.glNewList(mobBox, GL11.GL_COMPILE);
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
		
		GL11.glDeleteLists(mobBox, 1);
		mobBox = 0;
	}
	
	@Override
	public void onUpdate()
	{
		mobs.clear();
		Stream<EntityLiving> stream = WMinecraft.getWorld().loadedEntityList
			.parallelStream().filter(e -> e instanceof EntityLiving)
			.map(e -> (EntityLiving)e)
			.filter(e -> !e.isDead && e.getHealth() > 0);
		
		if(!wurst.special.targetSpf.invisibleMobs.isChecked())
			stream = stream.filter(e -> !e.isInvisible());
		
		mobs.addAll(stream.collect(Collectors.toList()));
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
		
		renderBoxes(partialTicks);
		
		if(tracers.isChecked())
			renderTracers(partialTicks);
		
		GL11.glPopMatrix();
		
		// GL resets
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
	}
	
	private void renderBoxes(double partialTicks)
	{
		for(EntityLiving e : mobs)
		{
			GL11.glPushMatrix();
			GL11.glTranslated(e.prevPosX + (e.posX - e.prevPosX) * partialTicks,
				e.prevPosY + (e.posY - e.prevPosY) * partialTicks,
				e.prevPosZ + (e.posZ - e.prevPosZ) * partialTicks);
			GL11.glScaled(e.width + 0.1, e.height + 0.1, e.width + 0.1);
			
			float f = WMinecraft.getPlayer().getDistanceToEntity(e) / 20F;
			GL11.glColor4f(2 - f, f, 0, 0.5F);
			
			GL11.glCallList(mobBox);
			
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
		for(EntityLiving e : mobs)
		{
			Vec3d end = e.getEntityBoundingBox().getCenter()
				.subtract(new Vec3d(e.posX, e.posY, e.posZ)
					.subtract(e.prevPosX, e.prevPosY, e.prevPosZ)
					.scale(1 - partialTicks));
			
			float f = WMinecraft.getPlayer().getDistanceToEntity(e) / 20F;
			GL11.glColor4f(2 - f, f, 0, 0.5F);
			
			GL11.glVertex3d(start.xCoord, start.yCoord, start.zCoord);
			GL11.glVertex3d(end.xCoord, end.yCoord, end.zCoord);
		}
		GL11.glEnd();
	}
}
