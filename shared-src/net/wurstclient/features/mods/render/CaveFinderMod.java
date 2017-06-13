/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.render;

import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.features.Category;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;

@SearchTags({"WallHack", "cave finder", "wall hack"})
@Mod.Bypasses
public final class CaveFinderMod extends Mod
{
	public CaveFinderMod()
	{
		super("CaveFinder", "Allows you to see caves through walls."
			+ (WMinecraft.OPTIFINE ? "\nNot compatible with shaders." : ""));
		setCategory(Category.RENDER);
	}
}
