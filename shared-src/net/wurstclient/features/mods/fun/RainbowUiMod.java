/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
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
		super("RainbowUI",
			"§cM§aa§9k§ce§as §9e§cv§ae§9r§cy§at§9h§ci§an§9g §cc§ao§9l§co§ar§9f§cu§al§9.");
		setCategory(Category.FUN);
	}
}
