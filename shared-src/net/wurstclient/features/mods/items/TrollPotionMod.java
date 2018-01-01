/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.items;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.features.Category;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;
import net.wurstclient.utils.ChatUtils;
import net.wurstclient.utils.InventoryUtils;

@SearchTags({"troll potion"})
@Mod.Bypasses
public final class TrollPotionMod extends Mod
{
	public TrollPotionMod()
	{
		super("TrollPotion",
			"Generates a potion with many annoying effects on it.");
		setCategory(Category.ITEMS);
	}
	
	@Override
	public void onEnable()
	{
		// check gamemode
		if(!WMinecraft.getPlayer().capabilities.isCreativeMode)
		{
			ChatUtils.error("Creative mode only.");
			setEnabled(false);
			return;
		}
		
		// generate potion
		ItemStack stack = InventoryUtils.createSplashPotion();
		NBTTagList effects = new NBTTagList();
		for(int i = 1; i <= 23; i++)
		{
			NBTTagCompound effect = new NBTTagCompound();
			effect.setInteger("Amplifier", Integer.MAX_VALUE);
			effect.setInteger("Duration", Integer.MAX_VALUE);
			effect.setInteger("Id", i);
			effects.appendTag(effect);
		}
		stack.setTagInfo("CustomPotionEffects", effects);
		stack.setStackDisplayName("§rSplash Potion of Trolling");
		
		// give potion
		if(InventoryUtils.placeStackInHotbar(stack))
			ChatUtils.message("Potion created.");
		else
			ChatUtils.error("Please clear a slot in your hotbar.");
		
		setEnabled(false);
	}
}
