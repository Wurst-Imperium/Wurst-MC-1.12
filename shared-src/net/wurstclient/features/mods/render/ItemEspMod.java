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
import net.wurstclient.compatibility.WEntityRenderer;
import net.wurstclient.compatibility.WItem;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.listeners.RenderListener;
import net.wurstclient.events.listeners.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Feature;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.update.Version;
import net.wurstclient.utils.RenderUtils;

@SearchTags({"item esp"})
@Mod.Bypasses
public final class ItemEspMod extends Mod
	implements UpdateListener, RenderListener
{
	private final CheckboxSetting names =
		new Version(WMinecraft.VERSION).isLowerThan("1.10") ? null
			: new CheckboxSetting("Show item names", true);
	
	private int itemBox;
	private final ArrayList<EntityItem> items = new ArrayList<>();
	
	public ItemEspMod()
	{
		super("ItemESP", "Highlights nearby items.");
		setCategory(Category.RENDER);
		
		if(names != null)
			addSetting(names);
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
	public void onRender(float partialTicks)
	{
		// GL settings
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glLineWidth(2);
		
		for(EntityItem e : items)
		{
			float x = (float)(e.prevPosX + (e.posX - e.prevPosX) * partialTicks
				- mc.getRenderManager().renderPosX);
			float y = (float)(e.prevPosY + (e.posY - e.prevPosY) * partialTicks
				- mc.getRenderManager().renderPosY);
			float z = (float)(e.prevPosZ + (e.posZ - e.prevPosZ) * partialTicks
				- mc.getRenderManager().renderPosZ);
			
			GL11.glPushMatrix();
			GL11.glTranslatef(x, y, z);
			GL11.glCallList(itemBox);
			GL11.glPopMatrix();
			
			if(names != null && names.isChecked())
			{
				ItemStack stack = e.getEntityItem();
				WEntityRenderer.drawNameplate(mc.fontRendererObj,
					WItem.getStackSize(stack) + "x " + stack.getDisplayName(),
					x, y + 1, z, 0, mc.getRenderManager().playerViewY,
					mc.getRenderManager().playerViewX,
					mc.gameSettings.thirdPersonView == 2, false);
				GL11.glDisable(GL11.GL_LIGHTING);
			}
		}
		
		// GL resets
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
	}
}
