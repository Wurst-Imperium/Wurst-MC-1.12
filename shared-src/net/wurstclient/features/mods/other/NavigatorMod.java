/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.other;

import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;
import net.wurstclient.navigator.NavigatorMainScreen;

@SearchTags({"ClickGUI", "click gui", "SearchGUI", "search gui", "HackMenu",
	"hack menu"})
@Mod.Bypasses
@Mod.DontSaveState
public final class NavigatorMod extends Mod
{
	public NavigatorMod()
	{
		super("Navigator", "");
	}
	
	@Override
	public void onEnable()
	{
		if(!(mc.currentScreen instanceof NavigatorMainScreen))
			mc.displayGuiScreen(new NavigatorMainScreen());
		setEnabled(false);
	}
}
