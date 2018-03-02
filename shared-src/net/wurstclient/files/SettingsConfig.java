/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.files;

import java.util.HashMap;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.wurstclient.WurstClient;
import net.wurstclient.features.Feature;
import net.wurstclient.settings.Setting;

public final class SettingsConfig extends Config
{
	public SettingsConfig()
	{
		super("settings.json");
	}
	
	@Override
	protected void loadFromJson(JsonElement json)
	{
		if(!json.isJsonObject())
			return;
		
		HashMap<String, Feature> features = new HashMap<>();
		for(Feature feature : WurstClient.INSTANCE.navigator.getList())
			features.put(feature.getName(), feature);
		
		for(Entry<String, JsonElement> e : json.getAsJsonObject().entrySet())
		{
			if(!e.getValue().isJsonObject())
				continue;
			
			Feature feature = features.get(e.getKey());
			if(feature == null)
				continue;
			
			HashMap<String, Setting> settings = new HashMap<>();
			for(Setting setting : feature.getSettings())
				settings.put(setting.getName().toLowerCase(), setting);
			
			for(Entry<String, JsonElement> e2 : e.getValue().getAsJsonObject()
				.entrySet())
			{
				String key = e2.getKey().toLowerCase();
				if(!settings.containsKey(key))
					continue;
				
				settings.get(key).fromJson(e2.getValue());
			}
		}
	}
	
	@Override
	protected JsonElement saveToJson()
	{
		JsonObject json = new JsonObject();
		for(Feature feature : WurstClient.INSTANCE.navigator.getList())
		{
			if(feature.getSettings().isEmpty())
				continue;
			
			JsonObject settings = new JsonObject();
			for(Setting setting : feature.getSettings())
				settings.add(setting.getName(), setting.toJson());
			
			json.add(feature.getName(), settings);
		}
		
		return json;
	}
}
