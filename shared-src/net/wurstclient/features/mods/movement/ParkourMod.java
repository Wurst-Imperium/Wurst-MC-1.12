/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.movement;

import net.wurstclient.WurstClient;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Mod;

@Mod.Bypasses
public final class ParkourMod extends Mod implements UpdateListener
{
	public ParkourMod()
	{
		super("Parkour",
			"Makes you jump automatically when reaching the edge of a block.\n"
				+ "Useful for parkours, Jump'n'Runs, etc.");
		setCategory(Category.MOVEMENT);
	}
	
	@Override
	public void onEnable()
	{
		WurstClient.INSTANCE.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		WurstClient.INSTANCE.events.remove(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		if(WMinecraft.getPlayer().onGround
			&& !WMinecraft.getPlayer().isSneaking()
			&& !mc.gameSettings.keyBindSneak.pressed
			&& !mc.gameSettings.keyBindJump.pressed
			&& WMinecraft.getWorld()
				.getCollisionBoxes(WMinecraft.getPlayer(),
					WMinecraft.getPlayer().getEntityBoundingBox()
						.offset(0, -0.5, 0).expand(-0.001, 0, -0.001))
				.isEmpty())
			WMinecraft.getPlayer().jump();
	}
}
