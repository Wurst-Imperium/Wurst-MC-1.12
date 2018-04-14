/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.other;

import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Feature;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;

@SearchTags({"auto switch"})
@Mod.Bypasses
public final class AutoSwitchMod extends Mod implements UpdateListener
{
	public AutoSwitchMod()
	{
		super("AutoSwitch", "Switches the item in your hand all the time.\n"
			+ "Tip: Use this in combination with BuildRandom while having a lot of different colored wool\n"
			+ "blocks in your hotbar.");
		setCategory(Category.OTHER);
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.mods.buildRandomMod};
	}
	
	@Override
	public void onEnable()
	{
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		if(WMinecraft.getPlayer().inventory.currentItem == 8)
			WMinecraft.getPlayer().inventory.currentItem = 0;
		else
			WMinecraft.getPlayer().inventory.currentItem++;
	}
}
