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

@SearchTags({"FastClimb", "fast ladder", "fast climb"})
@Mod.Bypasses(ghostMode = false, latestNCP = false)
public final class FastLadderMod extends Mod implements UpdateListener
{
	public FastLadderMod()
	{
		super("FastLadder", "Allows you to climb up ladders faster.");
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
		if(!WMinecraft.getPlayer().isOnLadder()
			|| !WMinecraft.getPlayer().isCollidedHorizontally)
			return;
		
		if(WMinecraft.getPlayer().movementInput.moveForward == 0
			&& WMinecraft.getPlayer().movementInput.moveStrafe == 0)
			return;
		
		WMinecraft.getPlayer().motionY = 0.2872;
	}
}
