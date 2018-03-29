/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.special_features;

import net.wurstclient.features.Feature;
import net.wurstclient.features.SearchTags;
import net.wurstclient.features.Spf;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.ModeSetting;

@SearchTags({"ArrayList", "HackList", "CheatList", "mod list", "array list",
	"hack list", "cheat list"})
public final class ModListSpf extends Spf
{
	private final ModeSetting mode = new ModeSetting("Mode",
		"§lAuto§r mode renders the whole list if it fits onto the screen.\n"
			+ "§lCount§r mode only renders the number of active mods.\n"
			+ "§lHidden§r mode renders nothing.",
		new String[]{"Auto", "Count", "Hidden"}, 0)
	{
		@Override
		public void update()
		{
			if(getSelected() == 0)
				animations.unlock();
			else
				animations.lock(() -> false);
		}
	};
	private final ModeSetting position =
		new ModeSetting("Position", new String[]{"Left", "Right"}, 0);
	private final CheckboxSetting animations =
		new CheckboxSetting("Animations", true);
	
	public ModListSpf()
	{
		super("ModList", "Shows a list of active mods on the screen.\n"
			+ "The §lLeft§r position should only be used while TabGui is disabled.");
		
		addSetting(mode);
		addSetting(position);
		addSetting(animations);
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.special.tabGuiSpf};
	}
	
	public boolean isCountMode()
	{
		return mode.getSelected() == 1;
	}
	
	public boolean isPositionRight()
	{
		return position.getSelected() == 1;
	}
	
	public boolean isHidden()
	{
		return mode.getSelected() == 2;
	}
	
	public boolean isAnimations()
	{
		return animations.isChecked();
	}
}
