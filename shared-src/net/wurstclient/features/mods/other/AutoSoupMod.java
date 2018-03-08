/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.other;

import net.minecraft.block.BlockContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemSoup;
import net.minecraft.item.ItemStack;
import net.wurstclient.compatibility.WBlock;
import net.wurstclient.compatibility.WItem;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.compatibility.WPlayerController;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Feature;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;
import net.wurstclient.features.special_features.YesCheatSpf.Profile;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;

@SearchTags({"AutoStew", "auto soup", "auto stew"})
@Mod.Bypasses
public final class AutoSoupMod extends Mod implements UpdateListener
{
	private final SliderSetting health =
		new SliderSetting("Health", 6.5, 0.5, 9.5, 0.5, ValueDisplay.DECIMAL);
	private final CheckboxSetting ignoreScreen =
		new CheckboxSetting("Ignore screen", true);
	
	private int oldSlot = -1;
	
	public AutoSoupMod()
	{
		super("AutoSoup",
			"Automatically eats soup if your health is lower than or equal to the set value.\n\n"
				+ "§lNote:§r This mod ignores hunger and assumes that eating soup directly refills your health.\n"
				+ "If the server you are playing on is not configured to do that, use AutoEat instead.");
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
		return new Feature[]{wurst.mods.autoSplashPotMod,
			wurst.mods.autoEatMod};
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
		stopIfEating();
	}
	
	@Override
	public void onUpdate()
	{
		// sort empty bowls
		for(int i = 0; i < 36; i++)
		{
			// filter out non-bowl items and empty bowl slot
			ItemStack stack =
				WMinecraft.getPlayer().inventory.getStackInSlot(i);
			if(stack == null || stack.getItem() != Items.BOWL || i == 9)
				continue;
			
			// check if empty bowl slot contains a non-bowl item
			ItemStack emptyBowlStack =
				WMinecraft.getPlayer().inventory.getStackInSlot(9);
			boolean swap = !WItem.isNullOrEmpty(emptyBowlStack)
				&& emptyBowlStack.getItem() != Items.BOWL;
			
			// place bowl in empty bowl slot
			WPlayerController.windowClick_PICKUP(i < 9 ? 36 + i : i);
			WPlayerController.windowClick_PICKUP(9);
			
			// place non-bowl item from empty bowl slot in current slot
			if(swap)
				WPlayerController.windowClick_PICKUP(i < 9 ? 36 + i : i);
		}
		
		// search soup in hotbar
		int soupInHotbar = findSoup(0, 9);
		
		// check if any soup was found
		if(soupInHotbar != -1)
		{
			// check if player should eat soup
			if(!shouldEatSoup())
			{
				stopIfEating();
				return;
			}
			
			// save old slot
			if(oldSlot == -1)
				oldSlot = WMinecraft.getPlayer().inventory.currentItem;
			
			// set slot
			WMinecraft.getPlayer().inventory.currentItem = soupInHotbar;
			
			// eat soup
			mc.gameSettings.keyBindUseItem.pressed = true;
			WPlayerController.processRightClick();
			
			return;
		}
		
		stopIfEating();
		
		// search soup in inventory
		int soupInInventory = findSoup(9, 36);
		
		// move soup in inventory to hotbar
		if(soupInInventory != -1)
			WPlayerController.windowClick_QUICK_MOVE(soupInInventory);
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
	
	private int findSoup(int startSlot, int endSlot)
	{
		for(int i = startSlot; i < endSlot; i++)
		{
			ItemStack stack =
				WMinecraft.getPlayer().inventory.getStackInSlot(i);
			
			if(stack != null && stack.getItem() instanceof ItemSoup)
				return i;
		}
		
		return -1;
	}
	
	private boolean shouldEatSoup()
	{
		// check health
		if(WMinecraft.getPlayer().getHealth() > health.getValueF() * 2F)
			return false;
		
		// check screen
		if(!ignoreScreen.isChecked() && mc.currentScreen != null)
			return false;
		
		// check for clickable objects
		if(mc.currentScreen == null && mc.objectMouseOver != null)
		{
			// clickable entities
			Entity entity = mc.objectMouseOver.entityHit;
			if(entity instanceof EntityVillager
				|| entity instanceof EntityTameable)
				return false;
			
			// clickable blocks
			if(mc.objectMouseOver.getBlockPos() != null && WBlock.getBlock(
				mc.objectMouseOver.getBlockPos()) instanceof BlockContainer)
				return false;
		}
		
		return true;
	}
	
	private void stopIfEating()
	{
		// check if eating
		if(oldSlot == -1)
			return;
		
		// stop eating
		mc.gameSettings.keyBindUseItem.pressed = false;
		
		// reset slot
		WMinecraft.getPlayer().inventory.currentItem = oldSlot;
		oldSlot = -1;
	}
}
