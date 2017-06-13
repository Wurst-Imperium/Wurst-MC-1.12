/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map.Entry;

import org.lwjgl.input.Keyboard;

import net.wurstclient.features.Cmd;
import net.wurstclient.files.ConfigFiles;
import net.wurstclient.utils.ChatUtils;
import net.wurstclient.utils.MiscUtils;

public final class BindsCmd extends Cmd
{
	public BindsCmd()
	{
		super("binds",
			"Allows you to manage keybinds through the chat.\n"
				+ "Multiple commands are separated by semicolons (;).",
			"set <key> <commands>", "add <key> <commands>", "list [<page>]",
			"clear <key>", "clear-all", "reset");
	}
	
	@Override
	public void execute(String[] args) throws CmdError
	{
		if(args.length == 0)
			syntaxError();
		
		switch(args[0].toLowerCase())
		{
			case "set":
			set(args);
			break;
			
			case "add":
			add(args);
			break;
			
			case "list":
			list(args);
			break;
			
			case "clear":
			clear(args);
			break;
			
			case "clear-all":
			wurst.keybinds.clear();
			ConfigFiles.KEYBINDS.save();
			ChatUtils.message("All keybinds removed.");
			break;
			
			case "reset":
			wurst.keybinds.loadDefaults();
			ConfigFiles.KEYBINDS.save();
			ChatUtils.message("Default keybinds loaded.");
			break;
			
			default:
			syntaxError();
			break;
		}
	}
	
	private void set(String[] args) throws CmdError
	{
		if(args.length < 3)
			syntaxError();
		
		String key = args[1].toUpperCase();
		if(Keyboard.getKeyIndex(key) == Keyboard.KEY_NONE)
			syntaxError("Unknown key: " + key);
		String commands =
			String.join(" ", Arrays.copyOfRange(args, 2, args.length));
		
		wurst.keybinds.bind(key, commands.split(";"));
		ConfigFiles.KEYBINDS.save();
		ChatUtils.message("Keybind set: " + key + " -> " + commands);
	}
	
	private void add(String[] args) throws CmdError
	{
		if(args.length < 3)
			syntaxError();
		
		String key = args[1].toUpperCase();
		if(Keyboard.getKeyIndex(key) == Keyboard.KEY_NONE)
			syntaxError("Unknown key: " + key);
		String commands =
			String.join(" ", Arrays.copyOfRange(args, 2, args.length));
		
		for(String command : commands.split(";"))
			wurst.keybinds.addBindAllowMultiple(key, command);
		ConfigFiles.KEYBINDS.save();
		
		ChatUtils.message("Keybind updated: " + key + " -> "
			+ String.join(";", wurst.keybinds.get(key)));
	}
	
	private void list(String[] args) throws CmdError
	{
		int page;
		if(args.length < 2)
			page = 1;
		else if(MiscUtils.isInteger(args[1]))
			page = Integer.parseInt(args[1]);
		else
		{
			syntaxError("Invalid page: " + args[1]);
			return;
		}
		
		int pages = (int)Math.ceil(wurst.keybinds.size() / 8F);
		if(page > pages || page < 1)
			syntaxError("Invalid page: " + page);
		
		ChatUtils.message("Total: " + wurst.keybinds.size()
			+ (wurst.keybinds.size() == 1 ? " keybind" : " keybinds"));
		ChatUtils.message("Keybind List (page " + page + "/" + pages + ")");
		
		int i = 0;
		for(Entry<String, ArrayList<String>> entry : wurst.keybinds.entrySet())
		{
			i++;
			if(i <= (page - 1) * 8)
				continue;
			if(i > page * 8)
				break;
			
			ChatUtils.message(
				entry.getKey() + " -> " + String.join(";", entry.getValue()));
		}
	}
	
	private void clear(String[] args) throws CmdError
	{
		if(args.length != 2)
			syntaxError();
		
		String key = args[1].toUpperCase();
		if(Keyboard.getKeyIndex(key) == Keyboard.KEY_NONE)
			syntaxError("Unknown key: " + key);
		
		ArrayList<String> oldCommands = wurst.keybinds.get(key);
		if(oldCommands == null)
			error("Nothing to remove.");
		
		wurst.keybinds.unbind(key);
		ConfigFiles.KEYBINDS.save();
		ChatUtils.message(
			"Keybind removed: " + key + " -> " + String.join(";", oldCommands));
	}
}
