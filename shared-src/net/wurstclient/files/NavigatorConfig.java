/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.files;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.wurstclient.WurstClient;
import net.wurstclient.settings.Setting;

public final class NavigatorConfig extends Config
{
	public NavigatorConfig()
	{
		super("navigator.json");
	}
	
	@Override
	protected void loadFromJson(JsonElement json)
	{
		WurstClient.INSTANCE.navigator.forEach((feature) -> {
			
			String featureName = feature.getName();
			if(!json.getAsJsonObject().has(featureName))
				return;
			
			JsonObject jsonFeature =
				json.getAsJsonObject().get(featureName).getAsJsonObject();
			
			// load preference
			if(jsonFeature.has("preference"))
				WurstClient.INSTANCE.navigator.setPreference(featureName,
					jsonFeature.get("preference").getAsLong());
			
			// load settings
			if(jsonFeature.has("settings"))
			{
				JsonObject jsonSettings =
					jsonFeature.get("settings").getAsJsonObject();
				
				for(Setting setting : feature.getSettings())
					try
					{
						setting.legacyFromJson(jsonSettings);
					}catch(Exception e)
					{
						e.printStackTrace();
					}
			}
		});
	}
	
	@Override
	protected JsonElement saveToJson()
	{
		JsonObject json = new JsonObject();
		
		WurstClient.INSTANCE.navigator.forEach((feature) -> {
			
			JsonObject jsonFeature = new JsonObject();
			
			// save preference
			long preference =
				WurstClient.INSTANCE.navigator.getPreference(feature.getName());
			if(preference != 0L)
				jsonFeature.addProperty("preference", preference);
			
			if(!jsonFeature.entrySet().isEmpty())
				json.add(feature.getName(), jsonFeature);
		});
		
		return json;
	}
}
