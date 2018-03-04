/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.other;

import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayer;
import net.wurstclient.compatibility.WConnection;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.compatibility.WPlayerController;
import net.wurstclient.compatibility.WPotionEffects;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Feature;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;
import net.wurstclient.features.special_features.YesCheatSpf.Profile;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;
import net.wurstclient.utils.InventoryUtils;

@SearchTags({"AutoPotion", "auto potion", "auto splash potion"})
@Mod.Bypasses
public final class AutoSplashPotMod extends Mod implements UpdateListener
{
	private final SliderSetting health =
		new SliderSetting("Health", 6, 0.5, 9.5, 0.5, ValueDisplay.DECIMAL);
	private final CheckboxSetting ignoreScreen =
		new CheckboxSetting("Ignore screen", true);
	
	private int timer;
	
	public AutoSplashPotMod()
	{
		super("AutoSplashPot",
			"Automatically throws instant health splash potions if your health is lower than or equal to\n"
				+ "the set value.");
		setCategory(Category.OTHER);
	}
	
	@Override
	public void initSettings()
	{
		addSetting(health);
		addSetting(ignoreScreen);
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.mods.autoSoupMod, wurst.mods.potionSaverMod};
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
		timer = 0;
	}
	
	@Override
	public void onUpdate()
	{
		// search potion in hotbar
		int potionInHotbar = findPotion(0, 9);
		
		// check if any potion was found
		if(potionInHotbar != -1)
		{
			// check timer
			if(timer > 0)
			{
				timer--;
				return;
			}
			
			// check health
			if(WMinecraft.getPlayer().getHealth() > health.getValueF() * 2F)
				return;
			
			// check screen
			if(!ignoreScreen.isChecked() && mc.currentScreen != null)
				return;
			
			// save old slot
			int oldSlot = WMinecraft.getPlayer().inventory.currentItem;
			
			// throw potion in hotbar
			WMinecraft.getPlayer().inventory.currentItem = potionInHotbar;
			WConnection.sendPacket(
				new CPacketPlayer.Rotation(WMinecraft.getPlayer().rotationYaw,
					90.0F, WMinecraft.getPlayer().onGround));
			WPlayerController.processRightClick();
			
			// reset slot and rotation
			WMinecraft.getPlayer().inventory.currentItem = oldSlot;
			WConnection.sendPacket(
				new CPacketPlayer.Rotation(WMinecraft.getPlayer().rotationYaw,
					WMinecraft.getPlayer().rotationPitch,
					WMinecraft.getPlayer().onGround));
			
			// reset timer
			timer = 10;
			
			return;
		}
		
		// search potion in inventory
		int potionInInventory = findPotion(9, 36);
		
		// move potion in inventory to hotbar
		if(potionInInventory != -1)
			WPlayerController.windowClick_QUICK_MOVE(potionInInventory);
	}
	
	@Override
	public void onYesCheatUpdate(Profile profile)
	{
		switch(profile)
		{
			case GHOST_MODE:
			ignoreScreen.lock(() -> false);
			break;
			
			default:
			ignoreScreen.unlock();
			break;
		}
	}
	
	private int findPotion(int startSlot, int endSlot)
	{
		for(int i = startSlot; i < endSlot; i++)
		{
			ItemStack stack =
				WMinecraft.getPlayer().inventory.getStackInSlot(i);
			
			// filter out non-splash potion items
			if(!InventoryUtils.isSplashPotion(stack))
				continue;
			
			// search for instant health effects
			if(InventoryUtils.hasEffect(stack, WPotionEffects.INSTANT_HEALTH))
				return i;
		}
		
		return -1;
	}
}
