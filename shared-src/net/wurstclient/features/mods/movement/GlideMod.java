/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.movement;

import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.listeners.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Mod;

@Mod.Bypasses(ghostMode = false, latestNCP = false, olderNCP = false)
public final class GlideMod extends Mod implements UpdateListener
{
	public GlideMod()
	{
		super("Glide", "Makes you glide down slowly instead of falling.");
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
		if(!WMinecraft.getPlayer().isAirBorne
			|| WMinecraft.getPlayer().isInWater()
			|| WMinecraft.getPlayer().isInLava()
			|| WMinecraft.getPlayer().isOnLadder())
			return;
		
		if(WMinecraft.getPlayer().motionY >= 0)
			return;
		
		WMinecraft.getPlayer().motionY = -0.125F;
		WMinecraft.getPlayer().jumpMovementFactor *= 1.21337F;
	}
}
