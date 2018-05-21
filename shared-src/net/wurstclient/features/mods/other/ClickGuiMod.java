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
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;

@SearchTags({"click gui", "WindowGUI", "window gui", "HackMenu", "hack menu"})
@Mod.Bypasses
@Mod.DontSaveState
public final class ClickGuiMod extends Mod
{
	private final SliderSetting opacity = new SliderSetting("Opacity", 0.5,
		0.15, 0.85, 0.01, ValueDisplay.PERCENTAGE);
	
	private final SliderSetting bgRed = new SliderSetting("BG red",
		"Background red", 64, 0, 255, 1, ValueDisplay.INTEGER);
	private final SliderSetting bgGreen = new SliderSetting("BG green",
		"Background green", 64, 0, 255, 1, ValueDisplay.INTEGER);
	private final SliderSetting bgBlue = new SliderSetting("BG blue",
		"Background blue", 64, 0, 255, 1, ValueDisplay.INTEGER);
	
	private final SliderSetting acRed = new SliderSetting("AC red",
		"Accent red", 16, 0, 255, 1, ValueDisplay.INTEGER);
	private final SliderSetting acGreen = new SliderSetting("AC green",
		"Accent green", 16, 0, 255, 1, ValueDisplay.INTEGER);
	private final SliderSetting acBlue = new SliderSetting("AC blue",
		"Accent blue", 16, 0, 255, 1, ValueDisplay.INTEGER);
	
	public ClickGuiMod()
	{
		super("ClickGUI", "Window-based ClickGUI.");
		addSetting(opacity);
		addSetting(bgRed);
		addSetting(bgGreen);
		addSetting(bgBlue);
		addSetting(acRed);
		addSetting(acGreen);
		addSetting(acBlue);
	}
	
	@Override
	public void onEnable()
	{
		mc.displayGuiScreen(new ClickGuiScreen(wurst.getGui()));
		setEnabled(false);
	}
	
	public float getOpacity()
	{
		return opacity.getValueF();
	}
	
	public float[] getBgColor()
	{
		return new float[]{bgRed.getValueI() / 255F, bgGreen.getValueI() / 255F,
			bgBlue.getValueI() / 255F};
	}
	
	public float[] getAcColor()
	{
		return new float[]{acRed.getValueI() / 255F, acGreen.getValueI() / 255F,
			acBlue.getValueI() / 255F};
	}
}
