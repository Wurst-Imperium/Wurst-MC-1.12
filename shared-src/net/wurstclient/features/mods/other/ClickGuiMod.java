/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.other;

import net.wurstclient.clickgui.ClickGuiScreen;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;

@SearchTags({"click gui", "WindowGUI", "window gui", "HackMenu", "hack menu"})
@Mod.Bypasses
@Mod.DontSaveState
public final class ClickGuiMod extends Mod
{
	public ClickGuiMod()
	{
		super("ClickGUI", "Window-based ClickGUI.");
	}
	
	@Override
	public void onEnable()
	{
		mc.displayGuiScreen(new ClickGuiScreen(wurst.getGui()));
		setEnabled(false);
	}
}
