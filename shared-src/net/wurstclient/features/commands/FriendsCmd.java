/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.commands;

import java.util.Iterator;

import net.wurstclient.features.Cmd;
import net.wurstclient.features.HelpPage;
import net.wurstclient.files.ConfigFiles;
import net.wurstclient.utils.ChatUtils;
import net.wurstclient.utils.MiscUtils;

@HelpPage("Commands/friends")
public final class FriendsCmd extends Cmd
{
	public FriendsCmd()
	{
		super("friends", "Manages your friends list.",
			"(add | remove) <player>", "list [<page>]");
	}
	
	@Override
	public void call(String[] args) throws CmdException
	{
		if(args.length == 0)
			throw new CmdSyntaxError();
		if(args[0].equalsIgnoreCase("list"))
		{
			if(args.length == 1)
			{
				call(new String[]{"list", "1"});
				return;
			}
			int pages = (int)Math.ceil(wurst.friends.size() / 8D);
			if(MiscUtils.isInteger(args[1]))
			{
				int page = Integer.valueOf(args[1]);
				if(page > pages || page < 1)
					throw new CmdSyntaxError();
				ChatUtils.message("Current friends: " + wurst.friends.size());
				ChatUtils
					.message("Friends list (page " + page + "/" + pages + "):");
				Iterator<String> itr = wurst.friends.iterator();
				for(int i = 0; itr.hasNext(); i++)
				{
					String friend = itr.next();
					if(i >= (page - 1) * 8 && i < (page - 1) * 8 + 8)
						ChatUtils.message(friend);
				}
			}else
				throw new CmdSyntaxError();
		}else if(args.length < 2)
			throw new CmdSyntaxError();
		else if(args[0].equalsIgnoreCase("add"))
		{
			if(wurst.friends.contains(args[1]))
			{
				ChatUtils.error(
					"\"" + args[1] + "\" is already in your friends list.");
				return;
			}
			wurst.friends.add(args[1]);
			ConfigFiles.FRIENDS.save();
			ChatUtils.message("Added friend \"" + args[1] + "\".");
		}else if(args[0].equalsIgnoreCase("remove"))
		{
			if(wurst.friends.remove(args[1]))
			{
				ConfigFiles.FRIENDS.save();
				ChatUtils.message("Removed friend \"" + args[1] + "\".");
			}else
				ChatUtils
					.error("\"" + args[1] + "\" is not in your friends list.");
		}else
			throw new CmdSyntaxError();
	}
}
