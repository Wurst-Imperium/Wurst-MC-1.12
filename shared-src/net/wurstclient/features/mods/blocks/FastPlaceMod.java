/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.blocks;

import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Feature;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;

@SearchTags({"fast place"})
@Mod.Bypasses
public final class FastPlaceMod extends Mod implements UpdateListener
{
	public FastPlaceMod()
	{
		super("FastPlace", "Allows you to place blocks 5 times faster.\n"
			+ "Tip: This can speed up mods like AutoBuild.");
		setCategory(Category.BLOCKS);
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.mods.fastBreakMod, wurst.mods.autoBuildMod,
			wurst.mods.buildRandomMod};
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
		mc.rightClickDelayTimer = 0;
	}
}
