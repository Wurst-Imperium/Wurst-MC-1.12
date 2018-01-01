/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.movement;

import net.wurstclient.features.Category;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;

@SearchTags({"safe walk"})
@Mod.Bypasses
public final class SafeWalkMod extends Mod
{
	public SafeWalkMod()
	{
		super("SafeWalk", "Prevents you from falling off edges.");
		setCategory(Category.MOVEMENT);
	}
}
