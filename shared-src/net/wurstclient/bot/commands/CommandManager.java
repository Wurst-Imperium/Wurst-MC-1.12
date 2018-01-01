/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.bot.commands;

import java.util.Collection;
import java.util.Comparator;
import java.util.TreeMap;

import net.wurstclient.bot.commands.Command.Info;

public class CommandManager
{
	private final TreeMap<String, Command> commands =
		new TreeMap<>(new Comparator<String>()
		{
			@Override
			public int compare(String o1, String o2)
			{
				return o1.compareToIgnoreCase(o2);
			}
		});
	
	public CommandManager()
	{
		addCommand(new ChatCmd());
		addCommand(new FixmeCmd());
		addCommand(new HelpCmd());
		addCommand(new JoinCmd());
		addCommand(new LoginCmd());
		addCommand(new ProxyCmd());
		addCommand(new StopCmd());
	}
	
	public Command getCommandByClass(Class<?> commandClass)
	{
		return commands.get(commandClass.getAnnotation(Info.class).name());
	}
	
	public Command getCommandByName(String name)
	{
		return commands.get(name);
	}
	
	public Collection<Command> getAllCommands()
	{
		return commands.values();
	}
	
	public int countCommands()
	{
		return commands.size();
	}
	
	private void addCommand(Command commmand)
	{
		commands.put(commmand.getName(), commmand);
	}
}
