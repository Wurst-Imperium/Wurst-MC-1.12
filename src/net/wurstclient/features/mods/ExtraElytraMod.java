/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.wurstclient.compatibility.WConnection;
import net.wurstclient.compatibility.WMath;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.HelpPage;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;
import net.wurstclient.features.special_features.YesCheatSpf.Profile;
import net.wurstclient.settings.CheckboxSetting;

@SearchTags({"EasyElytra", "extra elytra", "easy elytra"})
@HelpPage("Mods/ExtraElytra")
@Mod.Bypasses
public final class ExtraElytraMod extends Mod implements UpdateListener
{
	private CheckboxSetting instantFly =
		new CheckboxSetting("Instant fly", true);
	private CheckboxSetting easyFly = new CheckboxSetting("Easy fly", false);
	private CheckboxSetting stopInWater =
		new CheckboxSetting("Stop flying in water", true);
	
	public ExtraElytraMod()
	{
		super("ExtraElytra", "Eases the use of the Elytra.");
		setCategory(Category.MOVEMENT);
	}
	
	@Override
	public void initSettings()
	{
		addSetting(instantFly);
		addSetting(easyFly);
		addSetting(stopInWater);
	}
	
	@Override
	public void onEnable()
	{
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		updateMS();
		
		ItemStack chest = WMinecraft.getPlayer()
			.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
		if(chest == null || chest.getItem() != Items.ELYTRA)
			return;
		
		if(WMinecraft.getPlayer().isElytraFlying())
		{
			if(stopInWater.isChecked() && WMinecraft.getPlayer().isInWater())
			{
				WConnection
					.sendPacket(new CPacketEntityAction(WMinecraft.getPlayer(),
						CPacketEntityAction.Action.START_FALL_FLYING));
				return;
			}
			
			if(easyFly.isChecked())
			{
				if(mc.gameSettings.keyBindJump.pressed)
					WMinecraft.getPlayer().motionY += 0.08;
				else if(mc.gameSettings.keyBindSneak.pressed)
					WMinecraft.getPlayer().motionY -= 0.04;
				
				if(mc.gameSettings.keyBindForward.pressed
					&& WMinecraft.getPlayer().getPosition().getY() < 256)
				{
					float yaw = (float)Math
						.toRadians(WMinecraft.getPlayer().rotationYaw);
					WMinecraft.getPlayer().motionX -= WMath.sin(yaw) * 0.05F;
					WMinecraft.getPlayer().motionZ += WMath.cos(yaw) * 0.05F;
				}else if(mc.gameSettings.keyBindBack.pressed
					&& WMinecraft.getPlayer().getPosition().getY() < 256)
				{
					float yaw = (float)Math
						.toRadians(WMinecraft.getPlayer().rotationYaw);
					WMinecraft.getPlayer().motionX += WMath.sin(yaw) * 0.05F;
					WMinecraft.getPlayer().motionZ -= WMath.cos(yaw) * 0.05F;
				}
			}
		}else if(instantFly.isChecked() && ItemElytra.isBroken(chest)
			&& mc.gameSettings.keyBindJump.pressed)
		{
			if(hasTimePassedM(1000))
			{
				updateLastMS();
				WMinecraft.getPlayer().setJumping(false);
				WMinecraft.getPlayer().setSprinting(true);
				WMinecraft.getPlayer().jump();
			}
			WConnection
				.sendPacket(new CPacketEntityAction(WMinecraft.getPlayer(),
					CPacketEntityAction.Action.START_FALL_FLYING));
		}
	}
	
	@Override
	public void onYesCheatUpdate(Profile profile)
	{
		switch(profile)
		{
			default:
			case OFF:
			case MINEPLEX:
			case ANTICHEAT:
			case OLDER_NCP:
			case LATEST_NCP:
			easyFly.unlock();
			break;
			case GHOST_MODE:
			easyFly.lock(() -> false);
			break;
		}
	}
}
