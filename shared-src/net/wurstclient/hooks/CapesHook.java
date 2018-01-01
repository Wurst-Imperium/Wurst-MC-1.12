/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.hooks;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;

import net.wurstclient.utils.JsonUtils;

public class CapesHook
{
	private static JsonObject capes;
	
	public static void checkCape(GameProfile player,
		Map<Type, MinecraftProfileTexture> map)
	{
		if(capes == null)
			try
			{
				// TODO: download capes to file
				HttpsURLConnection connection = (HttpsURLConnection)new URL(
					"https://www.wurstclient.net/api/v1/capes.json")
						.openConnection();
				connection.connect();
				capes = JsonUtils.jsonParser
					.parse(new InputStreamReader(connection.getInputStream()))
					.getAsJsonObject();
				
			}catch(Exception e)
			{
				System.err.println(
					"[Wurst] Failed to load capes from wurstclient.net!");
				e.printStackTrace();
				return;
			}
		
		// get cape from server
		try
		{
			if(capes.has(player.getName()))
				map.put(Type.CAPE, new MinecraftProfileTexture(
					capes.get(player.getName()).getAsString(), null));
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
