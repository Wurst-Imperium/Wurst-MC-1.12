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

@SearchTags({"auto sprint"})
@Mod.Bypasses
public final class AutoSprintMod extends Mod implements UpdateListener
{
	public AutoSprintMod()
	{
		super("AutoSprint", "Makes you sprint automatically.");
		setCategory(Category.MOVEMENT);
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
		if(WMinecraft.getPlayer().isCollidedHorizontally
			|| WMinecraft.getPlayer().isSneaking())
			return;
		
		if(WMinecraft.getPlayer().moveForward > 0)
			WMinecraft.getPlayer().setSprinting(true);
	}
}
