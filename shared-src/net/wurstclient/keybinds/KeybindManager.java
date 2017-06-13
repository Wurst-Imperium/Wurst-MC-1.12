/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.keybinds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.lwjgl.input.Keyboard;

import net.wurstclient.WurstClient;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.KeyPressEvent;
import net.wurstclient.files.ConfigFiles;

public class KeybindManager
{
	private final TreeMap<String, ArrayList<String>> map = new TreeMap<>();
	
	public void loadDefaults()
	{
		map.clear();
		bind("B", ".t fastplace", ".t fastbreak");
		bind("C", ".t fullbright");
		bind("G", ".t flight");
		bind("GRAVE", ".t speednuker");
		bind("H", ".t /home");
		bind("J", ".t jesus");
		bind("K", ".t multiaura");
		bind("L", ".t nuker");
		bind("LCONTROL", ".t navigator");
		bind("R", ".t killaura");
		bind("RSHIFT", ".t navigator");
		bind("U", ".t freecam");
		bind("X", ".t x-ray");
		bind("Z", ".t sneak");
	}
	
	public void bind(String key, String... commands)
	{
		bind(key, Arrays.asList(commands));
	}
	
	public void bind(String key, Collection<String> commands)
	{
		map.put(key, new ArrayList<>(commands));
	}
	
	public void unbind(String key)
	{
		map.remove(key);
	}
	
	public void addBind(String key, String command)
	{
		ArrayList<String> commands = map.get(key);
		if(commands == null)
			bind(key, command);
		else if(!commands.contains(command))
			commands.add(command);
	}
	
	public void addBindAllowMultiple(String key, String command)
	{
		ArrayList<String> commands = map.get(key);
		if(commands == null)
			bind(key, command);
		else
			commands.add(command);
	}
	
	public void removeBind(String key, String command)
	{
		ArrayList<String> commands = map.get(key);
		if(commands == null)
			return;
		
		while(commands.contains(command))
			commands.remove(command);
		if(commands.isEmpty())
			map.remove(key);
	}
	
	public void forceAddGuiKeybind()
	{
		for(ArrayList<String> value : map.values())
			if(value.contains(".t navigator"))
				return;
			
		addBind("LCONTROL", ".t navigator");
		ConfigFiles.KEYBINDS.save();
	}
	
	public int size()
	{
		return map.size();
	}
	
	public ArrayList<String> get(Object key)
	{
		return map.get(key);
	}
	
	public void clear()
	{
		map.clear();
	}
	
	public Set<Entry<String, ArrayList<String>>> entrySet()
	{
		return map.entrySet();
	}
	
	public Set<String> keySet()
	{
		return map.keySet();
	}
	
	public void onKeyPress()
	{
		if(!WurstClient.INSTANCE.isEnabled())
			return;
		
		int keyCode = Keyboard.getEventKey();
		if(keyCode == 0)
			return;
		String keyName = Keyboard.getKeyName(keyCode);
		
		KeyPressEvent event = new KeyPressEvent(keyCode, keyName);
		WurstClient.INSTANCE.events.fire(event);
		
		ArrayList<String> commands = map.get(keyName);
		if(commands == null)
			return;
		
		new ArrayList<>(commands).forEach(
			cmd -> WMinecraft.getPlayer().sendAutomaticChatMessage(cmd));
	}
}
