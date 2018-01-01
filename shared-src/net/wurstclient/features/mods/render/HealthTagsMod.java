/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.render;

import net.wurstclient.features.Category;
import net.wurstclient.features.Feature;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;

@SearchTags({"health tags"})
@Mod.Bypasses
public final class HealthTagsMod extends Mod
{
	public HealthTagsMod()
	{
		super("HealthTags", "Shows the health of players in their nametags.");
		setCategory(Category.RENDER);
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.mods.nameTagsMod};
	}
}
