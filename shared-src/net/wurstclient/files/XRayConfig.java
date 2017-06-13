/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.files;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import net.minecraft.block.Block;
import net.wurstclient.compatibility.WBlock;
import net.wurstclient.features.mods.render.XRayMod;
import net.wurstclient.utils.XRayUtils;

public class XRayConfig extends Config
{
	public XRayConfig()
	{
		super("xray.json");
	}
	
	@Override
	protected void loadFromJson(JsonElement json)
	{
		XRayMod.xrayBlocks.clear();
		
		for(JsonElement element : json.getAsJsonArray())
			try
			{
				XRayMod.xrayBlocks
					.add(Block.getBlockFromName(element.getAsString()));
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		
		XRayUtils.sortBlocks();
	}
	
	@Override
	protected JsonElement saveToJson()
	{
		XRayUtils.sortBlocks();
		
		JsonArray json = new JsonArray();
		for(Block block : XRayMod.xrayBlocks)
			json.add(new JsonPrimitive(WBlock.getName(block)));
		
		return json;
	}
}
