/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.combat;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.GUIRenderListener;
import net.wurstclient.events.RenderListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Feature;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;
import net.wurstclient.font.Fonts;
import net.wurstclient.utils.EntityUtils;
import net.wurstclient.utils.EntityUtils.TargetSettings;
import net.wurstclient.utils.RenderUtils;
import net.wurstclient.utils.RotationUtils;

@SearchTags({"bow aimbot"})
@Mod.Bypasses
public final class BowAimbotMod extends Mod
	implements UpdateListener, RenderListener, GUIRenderListener
{
	private static final AxisAlignedBB TARGET_BOX =
		new AxisAlignedBB(-0.5, -0.5, -0.5, 0.5, 0.5, 0.5);
	
	private Entity target;
	private float velocity;
	
	private final TargetSettings targetSettings = new TargetSettings();
	
	public BowAimbotMod()
	{
		super("BowAimbot",
			"Automatically aims your bow at the closest entity.\n"
				+ "Tip: This works with FastBow.");
		setCategory(Category.COMBAT);
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.mods.fastBowMod};
	}
	
	@Override
	public void onEnable()
	{
		wurst.events.add(GUIRenderListener.class, this);
		wurst.events.add(RenderListener.class, this);
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(GUIRenderListener.class, this);
		wurst.events.remove(RenderListener.class, this);
		wurst.events.remove(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		// check if using item
		if(!mc.gameSettings.keyBindUseItem.pressed)
		{
			target = null;
			return;
		}
		if(!WMinecraft.getPlayer().isHandActive()
			&& !wurst.mods.fastBowMod.isActive())
		{
			target = null;
			return;
		}
		
		// check if item is bow
		ItemStack item = WMinecraft.getPlayer().inventory.getCurrentItem();
		if(item == null || !(item.getItem() instanceof ItemBow))
		{
			target = null;
			return;
		}
		
		// set target
		if(!EntityUtils.isCorrectEntity(target, targetSettings))
			target = EntityUtils.getBestEntityToAttack(targetSettings);
		if(target == null)
			return;
		
		// set velocity
		velocity = (72000 - WMinecraft.getPlayer().getItemInUseCount()) / 20F;
		velocity = (velocity * velocity + velocity * 2) / 3;
		if(velocity > 1)
			velocity = 1;
		
		// adjust for FastBow
		if(wurst.mods.fastBowMod.isActive())
			velocity = 1;
		
		// set position to aim at
		double d = RotationUtils.getEyesPos()
			.distanceTo(target.boundingBox.getCenter());
		double posX = target.posX + (target.posX - target.prevPosX) * d
			- WMinecraft.getPlayer().posX;
		double posY = target.posY + (target.posY - target.prevPosY) * d
			+ target.height * 0.5 - WMinecraft.getPlayer().posY
			- WMinecraft.getPlayer().getEyeHeight();
		double posZ = target.posZ + (target.posZ - target.prevPosZ) * d
			- WMinecraft.getPlayer().posZ;
		
		// set yaw
		WMinecraft.getPlayer().rotationYaw =
			(float)Math.toDegrees(Math.atan2(posZ, posX)) - 90;
		
		// calculate needed pitch
		double hDistance = Math.sqrt(posX * posX + posZ * posZ);
		double hDistanceSq = hDistance * hDistance;
		float g = 0.006F;
		float velocitySq = velocity * velocity;
		float velocityPow4 = velocitySq * velocitySq;
		float neededPitch = (float)-Math.toDegrees(Math.atan((velocitySq - Math
			.sqrt(velocityPow4 - g * (g * hDistanceSq + 2 * posY * velocitySq)))
			/ (g * hDistance)));
		
		// set pitch
		if(Float.isNaN(neededPitch))
			RotationUtils.faceEntityClient(target);
		else
			WMinecraft.getPlayer().rotationPitch = neededPitch;
	}
	
	@Override
	public void onRender(float partialTicks)
	{
		if(target == null)
			return;
		
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
		
		// set position
		GL11.glTranslated(target.posX, target.posY, target.posZ);
		
		// set size
		double boxWidth = target.width + 0.1;
		double boxHeight = target.height + 0.1;
		GL11.glScaled(boxWidth, boxHeight, boxWidth);
		
		// move to center
		GL11.glTranslated(0, 0.5, 0);
		
		double v = 1 / velocity;
		GL11.glScaled(v, v, v);
		
		// draw outline
		GL11.glColor4d(1, 0, 0, 0.5F * velocity);
		RenderUtils.drawOutlinedBox(TARGET_BOX);
		
		// draw box
		GL11.glColor4d(1, 0, 0, 0.25F * velocity);
		RenderUtils.drawSolidBox(TARGET_BOX);
		
		GL11.glPopMatrix();
		
		// GL resets
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
	}
	
	@Override
	public void onRenderGUI()
	{
		if(target == null)
			return;
		
		// GL settings
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_CULL_FACE);
		
		GL11.glPushMatrix();
		
		String message;
		if(velocity < 1)
			message = "Charging: " + (int)(velocity * 100) + "%";
		else
			message = "Ready To Shoot";
		
		// translate to center
		ScaledResolution sr = new ScaledResolution(mc);
		int msgWidth = Fonts.segoe15.getStringWidth(message);
		GL11.glTranslated(sr.getScaledWidth() / 2 - msgWidth / 2,
			sr.getScaledHeight() / 2 + 1, 0);
		
		// background
		GL11.glColor4f(0, 0, 0, 0.5F);
		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glVertex2d(0, 0);
			GL11.glVertex2d(msgWidth + 3, 0);
			GL11.glVertex2d(msgWidth + 3, 10);
			GL11.glVertex2d(0, 10);
		}
		GL11.glEnd();
		
		// text
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		Fonts.segoe15.drawString(message, 2, -1, 0xffffffff);
		
		GL11.glPopMatrix();
		
		// GL resets
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_BLEND);
	}
}
