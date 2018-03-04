/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.fun;

import java.util.Random;

import net.minecraft.network.play.client.CPacketPlayer;
import net.wurstclient.compatibility.WConnection;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;

@SearchTags({"Retarded"})
@Mod.Bypasses(ghostMode = false, latestNCP = false, olderNCP = false)
public final class DerpMod extends Mod implements UpdateListener
{
	private final Random random = new Random();
	
	public DerpMod()
	{
		super("Derp", "Randomly moves your head around.");
		setCategory(Category.FUN);
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
		float yaw = WMinecraft.getPlayer().rotationYaw
			+ random.nextFloat() * 360F - 180F;
		float pitch = random.nextFloat() * 180F - 90F;
		
		WConnection.sendPacket(new CPacketPlayer.Rotation(yaw, pitch,
			WMinecraft.getPlayer().onGround));
	}
}
