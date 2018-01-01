/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.commands;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagString;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.features.Cmd;
import net.wurstclient.features.HelpPage;

@HelpPage("Commands/author")
public final class AuthorCmd extends Cmd
{
	public AuthorCmd()
	{
		super("author", "Changes the held book's author.", "<author>");
	}
	
	@Override
	public void call(String[] args) throws CmdException
	{
		if(args.length == 0)
			throw new CmdSyntaxError();
		if(!WMinecraft.getPlayer().capabilities.isCreativeMode)
			throw new CmdError("Creative mode only.");
		ItemStack item = WMinecraft.getPlayer().inventory.getCurrentItem();
		if(item == null || Item.getIdFromItem(item.getItem()) != 387)
			throw new CmdError(
				"You are not holding a written book in your hand.");
		String author = args[0];
		for(int i = 1; i < args.length; i++)
			author += " " + args[i];
		item.setTagInfo("author", new NBTTagString(author));
	}
}
