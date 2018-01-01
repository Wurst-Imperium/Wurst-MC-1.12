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

@SearchTags({"KillerPotion", "kill potion", "killer potion"})
@Mod.Bypasses
public final class KillPotionMod extends Mod
{
	public KillPotionMod()
	{
		super("KillPotion",
			"Generates a potion that can kill anything, even players in Creative mode.\n"
				+ "Requires Creative mode.");
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
		NBTTagCompound effect = new NBTTagCompound();
		effect.setInteger("Amplifier", 125);
		effect.setInteger("Duration", 2000);
		effect.setInteger("Id", 6);
		NBTTagList effects = new NBTTagList();
		effects.appendTag(effect);
		stack.setTagInfo("CustomPotionEffects", effects);
		stack.setStackDisplayName("§rSplash Potion of §4§lDEATH");
		
		// give potion
		if(InventoryUtils.placeStackInHotbar(stack))
			ChatUtils.message("Potion created.");
		else
			ChatUtils.error("Please clear a slot in your hotbar.");
		
		setEnabled(false);
	}
}
