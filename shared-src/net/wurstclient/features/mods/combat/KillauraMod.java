/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.combat;

import java.util.Comparator;
import java.util.function.ToDoubleFunction;
import java.util.stream.Stream;

import org.lwjgl.opengl.GL11;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.compatibility.WPlayer;
import net.wurstclient.events.PostUpdateListener;
import net.wurstclient.events.RenderListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Feature;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;
import net.wurstclient.features.special_features.YesCheatSpf.Profile;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.EnumSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;
import net.wurstclient.utils.EntityUtils;
import net.wurstclient.utils.EntityUtils.TargetSettings;
import net.wurstclient.utils.RenderUtils;
import net.wurstclient.utils.RotationUtils;

@SearchTags({"ForceField", "kill aura", "force field"})
@Mod.Bypasses
public final class KillauraMod extends Mod
	implements UpdateListener, PostUpdateListener, RenderListener
{
	public final CheckboxSetting useCooldown = !WMinecraft.COOLDOWN ? null
		: new CheckboxSetting("Use Attack Cooldown as Speed", true)
		{
			@Override
			public void update()
			{
				speed.setDisabled(isChecked());
			}
		};
	public final SliderSetting speed =
		new SliderSetting("Speed", 12, 0.1, 20, 0.1, ValueDisplay.DECIMAL);
	public final SliderSetting range =
		new SliderSetting("Range", 4.25, 1, 10, 0.05, ValueDisplay.DECIMAL);
	private final EnumSetting<Priority> priority = new EnumSetting<>("Priority",
		"Determines which entity will be attacked first.\n"
			+ "\u00a7lDistance\u00a7r - Attacks the closest entity.\n"
			+ "\u00a7lAngle\u00a7r - Attacks the entity that requires\n"
			+ "the least head movement.\n"
			+ "\u00a7lHealth\u00a7r - Attacks the weakest entity.",
		Priority.values(), Priority.ANGLE);
	public final SliderSetting fov =
		new SliderSetting("FOV", 360, 30, 360, 10, ValueDisplay.DEGREES);
	public final CheckboxSetting hitThroughWalls =
		new CheckboxSetting("Hit through walls", false);
	private final SliderSetting filterFlying =
		new SliderSetting("Filter flying",
			"Won't attack players that\n" + "are at least the given\n"
				+ "distance above ground.",
			0, 0, 2, 0.05,
			v -> v == 0 ? "off" : ValueDisplay.DECIMAL.getValueString(v));
	
	private final TargetSettings targetSettings = new TargetSettings()
	{
		@Override
		public boolean targetBehindWalls()
		{
			return hitThroughWalls.isChecked();
		}
		
		@Override
		public float getRange()
		{
			return range.getValueF();
		}
		
		@Override
		public float getFOV()
		{
			return fov.getValueF();
		}
		
		@Override
		public double getFilterFlying()
		{
			return filterFlying.getValue();
		}
	};
	
	private Entity attackTarget;
	private Entity renderTarget;
	
	public KillauraMod()
	{
		super("Killaura", "Automatically attacks entities around you.");
		setCategory(Category.COMBAT);
	}
	
	@Override
	public void initSettings()
	{
		if(useCooldown != null)
			addSetting(useCooldown);
		
		addSetting(speed);
		addSetting(range);
		addSetting(priority);
		addSetting(fov);
		addSetting(hitThroughWalls);
		addSetting(filterFlying);
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.special.targetSpf, wurst.special.yesCheatSpf,
			wurst.mods.criticalsMod};
	}
	
	@Override
	public void onEnable()
	{
		// disable other killauras
		wurst.mods.clickAuraMod.setEnabled(false);
		wurst.mods.fightBotMod.setEnabled(false);
		wurst.mods.killauraLegitMod.setEnabled(false);
		wurst.mods.multiAuraMod.setEnabled(false);
		wurst.mods.protectMod.setEnabled(false);
		wurst.mods.tpAuraMod.setEnabled(false);
		wurst.mods.triggerBotMod.setEnabled(false);
		
		wurst.events.add(UpdateListener.class, this);
		wurst.events.add(PostUpdateListener.class, this);
		wurst.events.add(RenderListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
		wurst.events.remove(PostUpdateListener.class, this);
		wurst.events.remove(RenderListener.class, this);
		attackTarget = null;
	}
	
	@Override
	public void onUpdate()
	{
		// update timer
		updateMS();
		
		// check timer / cooldown
		if(useCooldown != null && useCooldown.isChecked()
			? WPlayer.getCooldown() < 1 : !hasTimePassedS(speed.getValueF()))
			return;
		
		// set entity
		Stream<Entity> stream = EntityUtils.getEntityStream(targetSettings);
		Entity entity =
			stream.min(priority.getSelected().comparator).orElse(null);
		renderTarget = entity;
		if(entity == null)
			return;
		
		// attack entity
		WPlayer.prepareAttack();
		if(!RotationUtils.faceEntityPacket(entity))
			return;
		attackTarget = entity;
		
		// reset timer
		updateLastMS();
	}
	
	@Override
	public void afterUpdate()
	{
		if(attackTarget == null)
			return;
		
		WPlayer.attackEntity(attackTarget);
		attackTarget = null;
	}
	
	@Override
	public void onRender(float partialTicks)
	{
		if(renderTarget == null || !(renderTarget instanceof EntityLivingBase))
			return;
		
		EntityLivingBase target = (EntityLivingBase)renderTarget;
		
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
		
		AxisAlignedBB box = new AxisAlignedBB(BlockPos.ORIGIN);
		float p = (target.getMaxHealth() - target.getHealth())
			/ target.getMaxHealth();
		float red = p * 2F;
		float green = 2 - red;
		
		GL11.glTranslated(target.posX, target.posY, target.posZ);
		GL11.glTranslated(0, 0.05, 0);
		GL11.glScaled(target.width, target.height, target.width);
		GL11.glTranslated(-0.5, 0, -0.5);
		
		if(p < 1)
		{
			GL11.glTranslated(0.5, 0.5, 0.5);
			GL11.glScaled(p, p, p);
			GL11.glTranslated(-0.5, -0.5, -0.5);
		}
		
		GL11.glColor4f(red, green, 0, 0.25F);
		RenderUtils.drawSolidBox(box);
		
		GL11.glColor4f(red, green, 0, 0.5F);
		RenderUtils.drawOutlinedBox(box);
		
		GL11.glPopMatrix();
		
		// GL resets
		GL11.glColor4f(1, 1, 1, 1);
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
			speed.resetUsableMax();
			range.resetUsableMax();
			break;
			
			case ANTICHEAT:
			case OLDER_NCP:
			case LATEST_NCP:
			case GHOST_MODE:
			speed.setUsableMax(12);
			range.setUsableMax(4.25);
			break;
		}
	}
	
	private enum Priority
	{
		DISTANCE("Distance",
			e -> WMinecraft.getPlayer().getDistanceSqToEntity(e)),
		
		ANGLE("Angle",
			e -> RotationUtils.getAngleToServerRotation(
				e.getEntityBoundingBox().getCenter())),
		
		HEALTH("Health", e -> {
			if(e instanceof EntityLivingBase)
				return ((EntityLivingBase)e).getHealth();
			else
				return Double.POSITIVE_INFINITY;
		});
		
		private final String name;
		private final Comparator<Entity> comparator;
		
		private Priority(String name, ToDoubleFunction<Entity> keyExtractor)
		{
			this.name = name;
			comparator = Comparator.comparingDouble(keyExtractor);
		}
		
		@Override
		public String toString()
		{
			return name;
		}
	}
}
