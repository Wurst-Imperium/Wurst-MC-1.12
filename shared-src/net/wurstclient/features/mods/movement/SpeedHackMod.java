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

@SearchTags({"speed hack"})
@Mod.Bypasses(ghostMode = false, latestNCP = false)
public final class SpeedHackMod extends Mod implements UpdateListener
{
	public SpeedHackMod()
	{
		super("SpeedHack",
			"Allows you to run roughly 2.5x faster than you would by sprinting and jumping.\n\n"
				+ "§6§lNotice:§r This mod was patched in NoCheat+ version 3.13.2. It will only bypass older versions\n"
				+ "of NoCheat+. Type §l/ncp version§r to check the NoCheat+ version of a server.");
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
		// return if sneaking or not walking
		if(WMinecraft.getPlayer().isSneaking()
			|| WMinecraft.getPlayer().moveForward == 0
				&& WMinecraft.getPlayer().moveStrafing == 0)
			return;
		
		// activate sprint if walking forward
		if(WMinecraft.getPlayer().moveForward > 0
			&& !WMinecraft.getPlayer().isCollidedHorizontally)
			WMinecraft.getPlayer().setSprinting(true);
		
		// activate mini jump if on ground
		if(WMinecraft.getPlayer().onGround)
		{
			WMinecraft.getPlayer().motionY += 0.1;
			WMinecraft.getPlayer().motionX *= 1.8;
			WMinecraft.getPlayer().motionZ *= 1.8;
			double currentSpeed =
				Math.sqrt(Math.pow(WMinecraft.getPlayer().motionX, 2)
					+ Math.pow(WMinecraft.getPlayer().motionZ, 2));
			
			// limit speed to highest value that works on NoCheat+ version
			// 3.13.0-BETA-sMD5NET-b878
			// UPDATE: Patched in NoCheat+ version 3.13.2-SNAPSHOT-sMD5NET-b888
			double maxSpeed = 0.66F;
			if(currentSpeed > maxSpeed)
			{
				WMinecraft.getPlayer().motionX =
					WMinecraft.getPlayer().motionX / currentSpeed * maxSpeed;
				WMinecraft.getPlayer().motionZ =
					WMinecraft.getPlayer().motionZ / currentSpeed * maxSpeed;
			}
		}
	}
}
