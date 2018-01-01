/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.options;

import java.util.TreeSet;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.wurstclient.WurstClient;
import net.wurstclient.files.ConfigFiles;

public class FriendsList extends TreeSet<String>
{
	public void middleClick(Entity entityHit)
	{
		if(entityHit == null || !(entityHit instanceof EntityPlayer))
			return;
		
		if(!WurstClient.INSTANCE.options.middleClickFriends)
			return;
		
		String name = entityHit.getName();
		
		if(WurstClient.INSTANCE.friends.contains(name))
			WurstClient.INSTANCE.friends.remove(name);
		else
			WurstClient.INSTANCE.friends.add(name);
		
		ConfigFiles.FRIENDS.save();
	}
}
