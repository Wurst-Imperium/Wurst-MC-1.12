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
import net.wurstclient.settings.ModeSetting;

@SearchTags({"tab gui", "HackMenu", "hack menu", "SideBar", "side bar",
	"blocks movement combat render chat fun items retro other"})
public final class TabGuiSpf extends Spf
{
	private final ModeSetting status =
		new ModeSetting("Status", new String[]{"Enabled", "Disabled"}, 1);
	
	public TabGuiSpf()
	{
		super("TabGui", "Allows you to quickly toggle mods while playing.\n"
			+ "Use the arrow keys to navigate.");
		
		addSetting(status);
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.special.modListSpf};
	}
	
	public boolean isHidden()
	{
		return status.getSelected() == 1;
	}
}
