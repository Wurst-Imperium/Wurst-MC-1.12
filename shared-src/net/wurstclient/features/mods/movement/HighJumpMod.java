/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.movement;

import net.wurstclient.features.Category;
import net.wurstclient.features.Feature;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;

@SearchTags({"high jump"})
@Mod.Bypasses(ghostMode = false,
	latestNCP = false,
	olderNCP = false,
	antiCheat = false,
	mineplex = false)
public final class HighJumpMod extends Mod
{
	private final SliderSetting height =
		new SliderSetting("Height", 6, 1, 100, 1, ValueDisplay.INTEGER);
	
	public HighJumpMod()
	{
		super("HighJump", "Allows you to jump higher.\n\n"
			+ "§c§lWARNING:§r You will take fall damage if you don't use NoFall.");
		setCategory(Category.MOVEMENT);
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.mods.noFallMod};
	}
	
	@Override
	public void initSettings()
	{
		addSetting(height);
	}
	
	public double getAdditionalJumpMotion()
	{
		return isActive() ? height.getValue() * 0.1 : 0;
	}
}
