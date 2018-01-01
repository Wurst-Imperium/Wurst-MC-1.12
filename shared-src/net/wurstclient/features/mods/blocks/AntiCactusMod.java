/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.blocks;

import net.wurstclient.features.Category;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;

@SearchTags({"NoCactus", "anti cactus", "no cactus"})
@Mod.Bypasses(ghostMode = false, latestNCP = false)
public final class AntiCactusMod extends Mod
{
	public AntiCactusMod()
	{
		super("AntiCactus", "Protects you from cactus damage.");
		setCategory(Category.BLOCKS);
	}
}
