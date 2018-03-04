/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.movement;

import net.minecraft.client.settings.GameSettings;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Feature;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;

@SearchTags({"auto walk"})
@Mod.Bypasses
public final class AutoWalkMod extends Mod implements UpdateListener
{
	public AutoWalkMod()
	{
		super("AutoWalk", "Makes you walk automatically.");
		setCategory(Category.MOVEMENT);
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.mods.autoSprintMod, wurst.commands.goToCmd};
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
		
		// reset forward key
		mc.gameSettings.keyBindForward.pressed =
			GameSettings.isKeyDown(mc.gameSettings.keyBindForward);
	}
	
	@Override
	public void onUpdate()
	{
		// force-press forward key
		mc.gameSettings.keyBindForward.pressed = true;
	}
}
