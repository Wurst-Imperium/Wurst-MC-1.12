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

@SearchTags({"InventoryWalk", "MenuWalk", "inv walk", "inventory walk",
	"menu walk"})
@Mod.Bypasses
public final class InvWalkMod extends Mod
{
	public InvWalkMod()
	{
		super("InvWalk", "Allows you to walk while the inventory is open.");
		setCategory(Category.MOVEMENT);
	}
}
