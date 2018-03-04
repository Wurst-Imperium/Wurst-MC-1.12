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

@SearchTags({"Phaze"})
@Mod.Bypasses(ghostMode = false,
	latestNCP = false,
	antiCheat = false,
	mineplex = false)
public final class PhaseMod extends Mod implements UpdateListener
{
	public PhaseMod()
	{
		super("Phase",
			"Exploits a bug in older versions of NoCheat+ that allows you to glitch through blocks.");
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
		WMinecraft.getPlayer().noClip = false;
	}
	
	@Override
	public void onUpdate()
	{
		WMinecraft.getPlayer().noClip = true;
		WMinecraft.getPlayer().fallDistance = 0;
		WMinecraft.getPlayer().onGround = true;
	}
}
