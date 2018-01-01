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
import net.wurstclient.utils.ChatUtils;
import net.wurstclient.utils.MiscUtils;

@HelpPage("Commands/help")
public final class HelpCmd extends Cmd
{
	public HelpCmd()
	{
		super("help", "Shows the command list or the help for a command.",
			"[<page>]", "[<command>]");
	}
	
	@Override
	public void call(String[] args) throws CmdException
	{
		if(args.length == 0)
		{
			call(new String[]{"1"});
			return;
		}
		int pages = (int)Math.ceil(wurst.commands.countCommands() / 8D);
		if(MiscUtils.isInteger(args[0]))
		{
			int page = Integer.valueOf(args[0]);
			if(page > pages || page < 1)
				throw new CmdSyntaxError("Invalid page: " + page);
			ChatUtils.message(
				"Available commands: " + wurst.commands.countCommands());
			ChatUtils
				.message("Command list (page " + page + "/" + pages + "):");
			Iterator<Cmd> itr = wurst.commands.getAllCommands().iterator();
			for(int i = 0; itr.hasNext(); i++)
			{
				Cmd cmd = itr.next();
				if(i >= (page - 1) * 8 && i < (page - 1) * 8 + 8)
					ChatUtils.message(cmd.getCmdName());
			}
		}else
		{
			Cmd cmd = wurst.commands.getCommandByName(args[0]);
			if(cmd != null)
			{
				ChatUtils.message("Available help for ." + args[0] + ":");
				cmd.printHelp();
				cmd.printSyntax();
			}else
				throw new CmdError(
					"Command \"" + args[0] + "\" could not be found.");
		}
	}
}
