/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.other;

import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.features.Category;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;

@SearchTags({"potion saver"})
@Mod.Bypasses
public final class PotionSaverMod extends Mod
{
	public PotionSaverMod()
	{
		super("PotionSaver",
			"Freezes all potion effects while you are standing still.");
		setCategory(Category.OTHER);
	}
	
	public boolean isFrozen()
	{
		return isActive()
			&& !WMinecraft.getPlayer().getActivePotionEffects().isEmpty()
			&& WMinecraft.getPlayer().motionX == 0
			&& WMinecraft.getPlayer().motionZ == 0;
	}
}
