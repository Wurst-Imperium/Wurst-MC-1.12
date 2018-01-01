/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.bot.commands;

import java.util.Iterator;

import net.wurstclient.bot.WurstBot;
import net.wurstclient.utils.MiscUtils;

@Command.Info(help = "Shows the command list or the help for a command.",
	name = "help",
	syntax = {"[<page>]", "[<command>]"})
public class HelpCmd extends Command
{
	@Override
	public void execute(String[] args) throws CmdError
	{
		if(args.length == 0)
		{
			execute(new String[]{"1"});
			return;
		}
		int pages = (int)Math
			.ceil(WurstBot.getBot().getCommandManager().countCommands() / 8D);
		if(MiscUtils.isInteger(args[0]))
		{
			int page = Integer.valueOf(args[0]);
			if(page > pages || page < 1)
				syntaxError("Invalid page: " + page);
			System.out.println("Available commands: "
				+ WurstBot.getBot().getCommandManager().countCommands());
			System.out
				.println("Command list (page " + page + "/" + pages + "):");
			Iterator<Command> itr = WurstBot.getBot().getCommandManager()
				.getAllCommands().iterator();
			for(int i = 0; itr.hasNext(); i++)
			{
				Command cmd = itr.next();
				if(i >= (page - 1) * 8 && i < (page - 1) * 8 + 8)
					System.out.println(cmd.getName());
			}
		}else
		{
			Command cmd =
				WurstBot.getBot().getCommandManager().getCommandByName(args[0]);
			if(cmd != null)
			{
				System.out.println("Available help for \"" + args[0] + "\":");
				cmd.printHelp();
				cmd.printSyntax();
			}else
				syntaxError();
		}
	}
}
