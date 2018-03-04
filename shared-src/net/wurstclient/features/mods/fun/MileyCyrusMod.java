/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.fun;

import net.minecraft.client.settings.GameSettings;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;

@SearchTags({"miley cyrus", "twerk"})
@Mod.Bypasses
public final class MileyCyrusMod extends Mod implements UpdateListener
{
	private int timer;
	private final SliderSetting twerkSpeed =
		new SliderSetting("Twerk speed", 5, 1, 10, 1, ValueDisplay.INTEGER);
	
	public MileyCyrusMod()
	{
		super("MileyCyrus", "Makes you twerk.");
		setCategory(Category.FUN);
	}
	
	@Override
	public void initSettings()
	{
		addSetting(twerkSpeed);
	}
	
	@Override
	public void onEnable()
	{
		timer = 0;
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
		mc.gameSettings.keyBindSneak.pressed =
			GameSettings.isKeyDown(mc.gameSettings.keyBindSneak);
	}
	
	@Override
	public void onUpdate()
	{
		timer++;
		if(timer < 10 - twerkSpeed.getValueI())
			return;
		
		mc.gameSettings.keyBindSneak.pressed =
			!mc.gameSettings.keyBindSneak.pressed;
		timer = -1;
	}
}
