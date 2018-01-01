/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.blocks;

import net.wurstclient.features.Category;
import net.wurstclient.features.Feature;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;

@SearchTags({"ghost hand"})
@Mod.Bypasses(ghostMode = false)
public final class GhostHandMod extends Mod
{
	public GhostHandMod()
	{
		super("GhostHand",
			"Allows you to reach specific blocks through walls.\n"
				+ "Type §l.ghosthand id <block_id>§r or §l.ghosthand name <block_name>§r to specify it.");
		setCategory(Category.BLOCKS);
	}
	
	@Override
	public String getRenderName()
	{
		return getName() + " [" + wurst.options.ghostHandID + "]";
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.commands.ghostHandCmd};
	}
}
