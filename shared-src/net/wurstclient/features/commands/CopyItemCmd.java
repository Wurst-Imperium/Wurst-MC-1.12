/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.commands;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.item.ItemStack;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.features.Cmd;
import net.wurstclient.features.HelpPage;
import net.wurstclient.utils.ChatUtils;
import net.wurstclient.utils.InventoryUtils;

@HelpPage("Commands/copyitem")
public final class CopyItemCmd extends Cmd
{
	public CopyItemCmd()
	{
		super("copyitem",
			"Allows you to copy items that other people are holding\n"
				+ "or wearing. Requires creative mode.",
			"<player> (hand|head|chest|legs|feet)");
	}
	
	@Override
	public void call(String[] args) throws CmdException
	{
		if(args.length != 2)
			throw new CmdSyntaxError();
		if(!WMinecraft.getPlayer().capabilities.isCreativeMode)
			throw new CmdError("Creative mode only.");
		
		// find item
		ItemStack item = null;
		for(Object entity : WMinecraft.getWorld().loadedEntityList)
			if(entity instanceof EntityOtherPlayerMP)
			{
				EntityOtherPlayerMP player = (EntityOtherPlayerMP)entity;
				if(player.getName().equalsIgnoreCase(args[0]))
				{
					switch(args[1].toLowerCase())
					{
						case "hand":
						item = player.inventory.getCurrentItem();
						break;
						
						case "head":
						item = player.inventory.armorItemInSlot(3);
						break;
						
						case "chest":
						item = player.inventory.armorItemInSlot(2);
						break;
						
						case "legs":
						item = player.inventory.armorItemInSlot(1);
						break;
						
						case "feet":
						item = player.inventory.armorItemInSlot(0);
						break;
						
						default:
						throw new CmdSyntaxError();
					}
					break;
				}
			}
		if(item == null)
			throw new CmdError(
				"Player \"" + args[0] + "\" could not be found.");
		
		// give item
		if(InventoryUtils.placeStackInHotbar(item))
			ChatUtils.message("Item copied.");
		else
			throw new CmdError("Please clear a slot in your hotbar.");
	}
}
