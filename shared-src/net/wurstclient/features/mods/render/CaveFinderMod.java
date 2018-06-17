/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.render;

import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.SetOpaqueCubeListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;

@SearchTags({"WallHack", "cave finder", "wall hack"})
@Mod.Bypasses
public final class CaveFinderMod extends Mod implements SetOpaqueCubeListener
{
	public CaveFinderMod()
	{
		super("CaveFinder", "Allows you to see caves through walls."
			+ (WMinecraft.OPTIFINE ? "\nNot compatible with shaders." : ""));
		setCategory(Category.RENDER);
	}
	
	@Override
	public void onEnable()
	{
		wurst.events.add(SetOpaqueCubeListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(SetOpaqueCubeListener.class, this);
	}
	
	@Override
	public void onSetOpaqueCube(SetOpaqueCubeEvent event)
	{
		event.cancel();
	}
}
