/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.files;

import com.google.gson.JsonElement;

import net.wurstclient.WurstClient;
import net.wurstclient.options.OptionsManager;
import net.wurstclient.utils.JsonUtils;

public final class OptionsConfig extends Config
{
	public OptionsConfig()
	{
		super("options.json");
	}
	
	@Override
	protected void loadFromJson(JsonElement json)
	{
		OptionsManager newOptions =
			JsonUtils.gson.fromJson(json, OptionsManager.class);
		
		if(newOptions != null)
			WurstClient.INSTANCE.options = newOptions;
	}
	
	@Override
	protected JsonElement saveToJson()
	{
		return JsonUtils.gson.toJsonTree(WurstClient.INSTANCE.options);
	}
}
