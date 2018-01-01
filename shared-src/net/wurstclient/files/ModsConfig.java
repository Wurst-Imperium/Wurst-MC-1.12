/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.files;

import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.wurstclient.WurstClient;
import net.wurstclient.features.Mod;

public final class ModsConfig extends Config
{
	// TODO: Rename to enabled-mods.json
	public ModsConfig()
	{
		super("modules.json");
	}
	
	@Override
	protected void loadFromJson(JsonElement json)
	{
		for(Entry<String, JsonElement> entry : json.getAsJsonObject()
			.entrySet())
		{
			Mod mod = WurstClient.INSTANCE.mods.getModByName(entry.getKey());
			
			if(mod == null || !mod.isStateSaved())
				continue;
			JsonObject jsonMod = entry.getValue().getAsJsonObject();
			
			if(jsonMod.get("enabled").getAsBoolean())
				mod.enableOnStartup();
		}
	}
	
	@Override
	protected JsonElement saveToJson()
	{
		JsonObject json = new JsonObject();
		
		JsonObject jsonMod = new JsonObject();
		jsonMod.addProperty("enabled", true);
		
		for(Mod mod : WurstClient.INSTANCE.mods.getAllMods())
			if(mod.isEnabled() && mod.isStateSaved())
				json.add(mod.getName(), jsonMod);
			
		return json;
	}
}
