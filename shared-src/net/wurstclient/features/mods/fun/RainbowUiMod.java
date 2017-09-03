/*
 * Copyright © 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.fun;

import net.wurstclient.features.Category;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;

@SearchTags({"RainbowGUI", "rainbow ui", "rainbow gui", "rgb"})
@Mod.Bypasses
public final class RainbowUiMod extends Mod
{
	public RainbowUiMod()
	{
		super("RainbowUI", "Makes everything colorful.");
		setCategory(Category.FUN);
	}
}
