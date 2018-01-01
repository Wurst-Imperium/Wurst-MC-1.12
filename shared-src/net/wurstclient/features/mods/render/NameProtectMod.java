/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.features.Category;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;

@SearchTags({"name protect"})
@Mod.Bypasses
public final class NameProtectMod extends Mod
{
	public NameProtectMod()
	{
		super("NameProtect", "Hides all player names.");
		setCategory(Category.RENDER);
	}
	
	public String protect(String string)
	{
		if(!isActive() || WMinecraft.getPlayer() == null)
			return string;
		
		String me = Minecraft.getMinecraft().session.getUsername();
		if(string.contains(me))
			return string.replace(me, "§oMe§r");
		
		int i = 0;
		for(NetworkPlayerInfo info : WMinecraft.getConnection()
			.getPlayerInfoMap())
		{
			i++;
			String name = info.getPlayerNameForReal().replaceAll("§\\w", "");
			
			if(string.contains(name))
				return string.replace(name, "§oPlayer" + i + "§r");
		}
		for(EntityPlayer player : WMinecraft.getWorld().playerEntities)
		{
			i++;
			String name = player.getName();
			
			if(string.contains(name))
				return string.replace(name, "§oPlayer" + i + "§r");
		}
		
		return string;
	}
}
