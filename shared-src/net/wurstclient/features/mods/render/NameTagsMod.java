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

@SearchTags({"name tags"})
@Mod.Bypasses
public final class NameTagsMod extends Mod
{
	public NameTagsMod()
	{
		super("NameTags",
			"Changes the scale of the nametags so you can always read them.\n"
				+ "Also allows you to see the nametags of sneaking players.");
		setCategory(Category.RENDER);
	}
}
