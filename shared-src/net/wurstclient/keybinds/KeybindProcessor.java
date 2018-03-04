/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.keybinds;

import org.lwjgl.input.Keyboard;

import net.wurstclient.WurstClient;
import net.wurstclient.events.KeyPressListener.KeyPressEvent;
import net.wurstclient.features.Mod;
import net.wurstclient.features.commands.CmdManager;
import net.wurstclient.features.mods.ModManager;

public final class KeybindProcessor
{
	private final ModManager mods;
	private final KeybindList keybinds;
	private final CmdManager cmdProcessor;
	
	public KeybindProcessor(ModManager hax, KeybindList keybinds,
		CmdManager cmdProcessor)
	{
		mods = hax;
		this.keybinds = keybinds;
		this.cmdProcessor = cmdProcessor;
	}
	
	public void onKeyPress()
	{
		if(!WurstClient.INSTANCE.isEnabled())
			return;
		
		int keyCode = Keyboard.getEventKey();
		if(keyCode == 0 || !Keyboard.getEventKeyState())
			return;
		String keyName = Keyboard.getKeyName(keyCode);
		
		KeyPressEvent event = new KeyPressEvent(keyCode, keyName);
		WurstClient.INSTANCE.events.fire(event);
		
		String commands = keybinds.getCommands(keyName);
		if(commands == null)
			return;
		
		commands = commands.replace(";", "§").replace("§§", ";");
		for(String command : commands.split("§"))
		{
			command = command.trim();
			
			if(command.startsWith("."))
				cmdProcessor.runCommand(command.substring(1));
			else if(command.contains(" "))
				cmdProcessor.runCommand(command);
			else
			{
				Mod mod = mods.getModByName(command);
				
				if(mod != null)
					mod.toggle();
				else
					cmdProcessor.runCommand(command);
			}
		}
	}
}
