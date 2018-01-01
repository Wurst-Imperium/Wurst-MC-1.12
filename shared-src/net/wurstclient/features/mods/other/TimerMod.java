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
import net.wurstclient.features.special_features.YesCheatSpf.Profile;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;

@Mod.Bypasses(ghostMode = false)
public final class TimerMod extends Mod
{
	private final SliderSetting speed =
		new SliderSetting("Speed", 2, 0.1, 20, 0.1, ValueDisplay.DECIMAL);
	
	public TimerMod()
	{
		super("Timer", "Changes the speed of almost everything.");
		setCategory(Category.OTHER);
	}
	
	@Override
	public String getRenderName()
	{
		return getName() + " [" + speed.getValueString() + "]";
	}
	
	@Override
	public void initSettings()
	{
		addSetting(speed);
	}
	
	@Override
	public void onYesCheatUpdate(Profile profile)
	{
		switch(profile)
		{
			case LATEST_NCP:
			case OLDER_NCP:
			speed.setUsableMax(1);
			break;
			
			default:
			speed.resetUsableMax();
			break;
		}
	}
	
	public float getTimerSpeed()
	{
		return isActive() ? speed.getValueF() : 1;
	}
}
