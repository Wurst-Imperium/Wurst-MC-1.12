/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.render;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.wurstclient.compatibility.WEntityRenderer;
import net.wurstclient.compatibility.WItem;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.CameraTransformViewBobbingListener;
import net.wurstclient.events.RenderListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Feature;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.update.Version;
import net.wurstclient.utils.RenderUtils;
import net.wurstclient.utils.RotationUtils;

@SearchTags({"item esp", "ItemTracers", "item tracers"})
@Mod.Bypasses
public final class ItemEspMod extends Mod implements UpdateListener,
	CameraTransformViewBobbingListener, RenderListener
{
	private final CheckboxSetting names =
		new Version(WMinecraft.VERSION).isLowerThan("1.10") ? null
			: new CheckboxSetting("Show item names", true);
	private final CheckboxSetting tracers =
		new CheckboxSetting("Tracers", "Draws lines to items.", false);
	
	private int itemBox;
	private final ArrayList<EntityItem> items = new ArrayList<>();
	
	public ItemEspMod()
	{
		super("ItemESP", "Highlights nearby items.");
		setCategory(Category.RENDER);
		
		if(names != null)
			addSetting(names);
		addSetting(tracers);
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.mods.chestEspMod, wurst.mods.mobEspMod,
			wurst.mods.playerEspMod};
	}
	
	@Override
	public void onEnable()
	{
		wurst.events.add(UpdateListener.class, this);
		wurst.events.add(CameraTransformViewBobbingListener.class, this);
		wurst.events.add(RenderListener.class, this);
		
		itemBox = GL11.glGenLists(1);
		GL11.glNewList(itemBox, GL11.GL_COMPILE);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glColor4f(1, 1, 0, 0.5F);
		RenderUtils.drawOutlinedBox(
			new AxisAlignedBB(-0.175, 0, -0.175, 0.175, 0.35, 0.175));
		GL11.glEndList();
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
		wurst.events.remove(CameraTransformViewBobbingListener.class, this);
		wurst.events.remove(RenderListener.class, this);
		
		GL11.glDeleteLists(itemBox, 1);
		itemBox = 0;
	}
	
	@Override
	public void onUpdate()
	{
		items.clear();
		for(Entity entity : WMinecraft.getWorld().loadedEntityList)
			if(entity instanceof EntityItem)
				items.add((EntityItem)entity);
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
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glLineWidth(2);
		
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
		for(EntityItem e : items)
		{
			GL11.glPushMatrix();
			GL11.glTranslated(e.prevPosX + (e.posX - e.prevPosX) * partialTicks,
				e.prevPosY + (e.posY - e.prevPosY) * partialTicks,
				e.prevPosZ + (e.posZ - e.prevPosZ) * partialTicks);
			
			GL11.glCallList(itemBox);
			
			if(names != null && names.isChecked())
			{
				ItemStack stack = e.getEntityItem();
				WEntityRenderer.drawNameplate(mc.fontRendererObj,
					WItem.getStackSize(stack) + "x " + stack.getDisplayName(),
					0, 1, 0, 0, mc.getRenderManager().playerViewY,
					mc.getRenderManager().playerViewX,
					mc.gameSettings.thirdPersonView == 2, false);
				GL11.glDisable(GL11.GL_LIGHTING);
			}
			
			GL11.glPopMatrix();
		}
	}
	
	private void renderTracers(double partialTicks)
	{
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glColor4f(1, 1, 0, 0.5F);
		
		Vec3d start = RotationUtils.getClientLookVec()
			.addVector(0, WMinecraft.getPlayer().getEyeHeight(), 0)
			.addVector(mc.getRenderManager().renderPosX,
				mc.getRenderManager().renderPosY,
				mc.getRenderManager().renderPosZ);
		
		GL11.glBegin(GL11.GL_LINES);
		for(EntityItem e : items)
		{
			Vec3d end = e.getEntityBoundingBox().getCenter()
				.subtract(new Vec3d(e.posX, e.posY, e.posZ)
					.subtract(e.prevPosX, e.prevPosY, e.prevPosZ)
					.scale(1 - partialTicks));
			
			GL11.glVertex3d(start.xCoord, start.yCoord, start.zCoord);
			GL11.glVertex3d(end.xCoord, end.yCoord, end.zCoord);
		}
		GL11.glEnd();
	}
}
