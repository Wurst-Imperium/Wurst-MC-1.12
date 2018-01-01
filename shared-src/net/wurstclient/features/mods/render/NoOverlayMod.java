/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.render;

import net.wurstclient.features.Category;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;

@SearchTags({"AntiPumkin", "no overlay"})
@Mod.Bypasses
public final class NoOverlayMod extends Mod
{
	public NoOverlayMod()
	{
		super("NoOverlay",
			"Blocks the overlays of pumpkins, water, fire, and lava.");
		setCategory(Category.RENDER);
	}
}
