/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.movement;

import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;
import net.wurstclient.features.special_features.YesCheatSpf.Profile;
import net.wurstclient.settings.CheckboxSetting;

@SearchTags({"no slowdown", "no slow down"})
@Mod.Bypasses(ghostMode = false)
public final class NoSlowdownMod extends Mod implements UpdateListener
{
	private final CheckboxSetting water =
		new CheckboxSetting("Block water slowness", false);
	private final CheckboxSetting soulSand =
		new CheckboxSetting("Block soul sand slowness", true);
	private final CheckboxSetting items =
		new CheckboxSetting("Block item slowness", true);
	
	public NoSlowdownMod()
	{
		super("NoSlowdown",
			"Cancels slowness effects caused by water, soul sand and using items.");
		setCategory(Category.MOVEMENT);
	}
	
	@Override
	public void initSettings()
	{
		addSetting(water);
		addSetting(soulSand);
		addSetting(items);
	}
	
	@Override
	public void onEnable()
	{
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		if(!water.isChecked())
			return;
		
		if(WMinecraft.getPlayer().onGround && WMinecraft.getPlayer().isInWater()
			&& mc.gameSettings.keyBindJump.pressed)
			WMinecraft.getPlayer().jump();
	}
	
	@Override
	public void onYesCheatUpdate(Profile profile)
	{
		switch(profile)
		{
			case LATEST_NCP:
			case OLDER_NCP:
			case ANTICHEAT:
			water.lock(() -> false);
			break;
			
			default:
			water.unlock();
			break;
		}
	}
	
	public boolean blockWaterSlowness()
	{
		return isActive() && water.isChecked();
	}
	
	public boolean blockSoulSandSlowness()
	{
		return isActive() && soulSand.isChecked();
	}
	
	public boolean blockItemSlowness()
	{
		return isActive() && items.isChecked();
	}
}
