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
import net.wurstclient.settings.ModeSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;

@SearchTags({"FastMine", "SpeedMine", "SpeedyGonzales", "fast break",
	"fast mine", "speed mine", "speedy gonzales"})
@Mod.Bypasses
public final class FastBreakMod extends Mod
{
	private final ModeSetting mode =
		new ModeSetting("Mode", new String[]{"Normal", "Instant"}, 1)
		{
			@Override
			public void update()
			{
				speed.setDisabled(getSelected() == 1);
			}
		};
	public final SliderSetting speed =
		new SliderSetting("Speed", 2, 1, 5, 0.05, ValueDisplay.DECIMAL);
	
	public FastBreakMod()
	{
		super("FastBreak", "Allows you to break blocks faster.\n"
			+ "Tip: This works with Nuker.");
		setCategory(Category.BLOCKS);
	}
	
	@Override
	public void initSettings()
	{
		addSetting(mode);
		addSetting(speed);
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.mods.fastPlaceMod, wurst.mods.autoMineMod,
			wurst.mods.nukerMod};
	}
	
	public float getHardnessModifier()
	{
		if(!isActive())
			return 1;
		
		if(mode.getSelected() != 0)
			return 1;
		
		return speed.getValueF();
	}
	
	public boolean shouldSpamPackets()
	{
		return isActive() && mode.getSelected() == 1;
	}
}
