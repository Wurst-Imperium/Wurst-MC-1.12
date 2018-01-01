/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.commands;

import java.util.Arrays;

import org.lwjgl.input.Keyboard;

import net.wurstclient.features.Cmd;
import net.wurstclient.keybinds.KeybindList.Keybind;
import net.wurstclient.utils.ChatUtils;
import net.wurstclient.utils.MiscUtils;

public final class BindsCmd extends Cmd
{
	public BindsCmd()
	{
		super("binds",
			"Allows you to manage keybinds through the chat.\n"
				+ "Multiple hacks/commands must be separated by ';'.",
			"add <key> <hacks>", "add <key> <commands>", "remove <key>",
			"list [<page>]", "remove-all", "reset");
	}
	
	@Override
	public void call(String[] args) throws CmdException
	{
		if(args.length < 1)
			throw new CmdSyntaxError();
		
		switch(args[0].toLowerCase())
		{
			case "add":
			add(args);
			break;
			
			case "remove":
			remove(args);
			break;
			
			case "list":
			list(args);
			break;
			
			case "remove-all":
			wurst.getKeybinds().removeAll();
			ChatUtils.message("All keybinds removed.");
			break;
			
			case "reset":
			wurst.getKeybinds().loadDefaults();
			ChatUtils.message("All keybinds reset to defaults.");
			break;
			
			default:
			throw new CmdSyntaxError();
		}
	}
	
	private void add(String[] args) throws CmdException
	{
		if(args.length < 3)
			throw new CmdSyntaxError();
		
		String key = args[1].toUpperCase();
		if(Keyboard.getKeyIndex(key) == Keyboard.KEY_NONE)
			throw new CmdSyntaxError("Unknown key: " + key);
		
		String commands =
			String.join(" ", Arrays.copyOfRange(args, 2, args.length));
		
		wurst.getKeybinds().add(key, commands);
		ChatUtils.message("Keybind set: " + key + " -> " + commands);
	}
	
	private void remove(String[] args) throws CmdException
	{
		if(args.length != 2)
			throw new CmdSyntaxError();
		
		String key = args[1].toUpperCase();
		if(Keyboard.getKeyIndex(key) == Keyboard.KEY_NONE)
			throw new CmdSyntaxError("Unknown key: " + key);
		
		String oldCommands = wurst.getKeybinds().getCommands(key);
		if(oldCommands == null)
			throw new CmdError("Nothing to remove.");
		
		wurst.getKeybinds().remove(key);
		ChatUtils.message("Keybind removed: " + key + " -> " + oldCommands);
	}
	
	private void list(String[] args) throws CmdException
	{
		if(args.length > 2)
			throw new CmdSyntaxError();
		
		int page;
		if(args.length < 2)
			page = 1;
		else if(MiscUtils.isInteger(args[1]))
			page = Integer.parseInt(args[1]);
		else
			throw new CmdSyntaxError("Not a number: " + args[1]);
		
		int keybinds = wurst.getKeybinds().size();
		int pages = Math.max((int)Math.ceil(keybinds / 8.0), 1);
		if(page > pages || page < 1)
			throw new CmdSyntaxError("Invalid page: " + page);
		
		ChatUtils.message(
			"Total: " + keybinds + (keybinds == 1 ? " keybind" : " keybinds"));
		ChatUtils.message("Keybind list (page " + page + "/" + pages + ")");
		
		for(int i = (page - 1) * 8; i < Math.min(page * 8, keybinds); i++)
		{
			Keybind k = wurst.getKeybinds().get(i);
			ChatUtils.message(k.getKey() + " -> " + k.getCommands());
		}
	}
}
