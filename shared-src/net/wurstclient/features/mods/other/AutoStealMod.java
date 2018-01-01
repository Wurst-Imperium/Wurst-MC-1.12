/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.other;

import net.wurstclient.features.Category;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;
import net.wurstclient.features.special_features.YesCheatSpf.Profile;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;

@SearchTags({"ChestStealer", "auto steal", "chest stealer"})
@Mod.Bypasses
public final class AutoStealMod extends Mod
{
	private final SliderSetting delay =
		new SliderSetting("Delay", 100, 0, 500, 10, ValueDisplay.INTEGER);
	private final CheckboxSetting buttons =
		new CheckboxSetting("Steal/Store buttons", true);
	
	public AutoStealMod()
	{
		super("AutoSteal",
			"Automatically steals everything from all chests you open.");
		setCategory(Category.OTHER);
	}
	
	@Override
	public void initSettings()
	{
		addSetting(buttons);
		addSetting(delay);
	}
	
	public boolean areButtonsVisible()
	{
		return buttons.isChecked();
	}
	
	public long getDelay()
	{
		return delay.getValueI();
	}
	
	@Override
	public void onYesCheatUpdate(Profile profile)
	{
		switch(profile)
		{
			case OFF:
			case MINEPLEX:
			delay.resetUsableMin();
			break;
			
			case ANTICHEAT:
			case OLDER_NCP:
			case LATEST_NCP:
			delay.setUsableMin(70);
			break;
			
			case GHOST_MODE:
			delay.setUsableMin(200);
			break;
		}
	}
}
