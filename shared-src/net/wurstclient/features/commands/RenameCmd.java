/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.commands;

import net.minecraft.item.ItemStack;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.features.Cmd;
import net.wurstclient.features.HelpPage;
import net.wurstclient.utils.ChatUtils;

@HelpPage("Commands/rename")
public final class RenameCmd extends Cmd
{
	public RenameCmd()
	{
		super("rename",
			"Renames the item in your hand. Use $ for colors, use $$ for $.",
			"<new_name>");
	}
	
	@Override
	public void call(String[] args) throws CmdException
	{
		if(!WMinecraft.getPlayer().capabilities.isCreativeMode)
			throw new CmdError("Creative mode only.");
		if(args.length == 0)
			throw new CmdSyntaxError();
		String message = args[0];
		for(int i = 1; i < args.length; i++)
			message += " " + args[i];
		message = message.replace("$", "§").replace("§§", "$");
		ItemStack item = WMinecraft.getPlayer().inventory.getCurrentItem();
		if(item == null)
			throw new CmdError("There is no item in your hand.");
		item.setStackDisplayName(message);
		ChatUtils.message("Renamed item to \"" + message + "§r\".");
	}
}
