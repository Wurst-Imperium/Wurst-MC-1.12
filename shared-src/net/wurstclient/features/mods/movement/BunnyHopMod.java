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
import net.wurstclient.features.Feature;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;

@SearchTags({"AutoJump", "BHop", "bunny hop", "auto jump"})
@Mod.Bypasses
public final class BunnyHopMod extends Mod implements UpdateListener
{
	public BunnyHopMod()
	{
		super("BunnyHop", "Automatically jumps whenever you walk.");
		setCategory(Category.MOVEMENT);
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.mods.autoSprintMod};
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
		// check onGround
		if(!WMinecraft.getPlayer().onGround)
			return;
		
		// check if sneaking
		if(WMinecraft.getPlayer().isSneaking())
			return;
		
		// check if moving
		if(WMinecraft.getPlayer().moveForward == 0
			&& WMinecraft.getPlayer().moveStrafing == 0)
			return;
		
		// jump
		WMinecraft.getPlayer().jump();
	}
}
