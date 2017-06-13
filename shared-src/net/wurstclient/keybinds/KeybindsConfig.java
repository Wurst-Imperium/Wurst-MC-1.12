/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.keybinds;

import java.util.ArrayList;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import net.wurstclient.WurstClient;
import net.wurstclient.files.Config;

public final class KeybindsConfig extends Config
{
	public KeybindsConfig()
	{
		super("keybinds.json");
	}
	
	@Override
	protected void loadFromJson(JsonElement json)
	{
		// clear keybinds
		WurstClient.INSTANCE.keybinds.clear();
		
		// add keybinds
		for(Entry<String, JsonElement> entry : json.getAsJsonObject()
			.entrySet())
		{
			ArrayList<String> commmands = new ArrayList<>();
			entry.getValue().getAsJsonArray()
				.forEach((c) -> commmands.add(c.getAsString()));
			WurstClient.INSTANCE.keybinds.bind(entry.getKey(), commmands);
		}
		
		// force-add GUI keybind if missing
		WurstClient.INSTANCE.keybinds.forceAddGuiKeybind();
	}
	
	@Override
	protected JsonElement saveToJson()
	{
		JsonObject json = new JsonObject();
		
		for(Entry<String, ArrayList<String>> entry : WurstClient.INSTANCE.keybinds
			.entrySet())
		{
			JsonArray commands = new JsonArray();
			entry.getValue().forEach((c) -> commands.add(new JsonPrimitive(c)));
			json.add(entry.getKey(), commands);
		}
		
		return json;
	}
}
