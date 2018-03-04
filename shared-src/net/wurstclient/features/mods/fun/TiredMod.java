/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.fun;

import net.minecraft.network.play.client.CPacketPlayer;
import net.wurstclient.compatibility.WConnection;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Mod;

@Mod.Bypasses(ghostMode = false, latestNCP = false, olderNCP = false)
public final class TiredMod extends Mod implements UpdateListener
{
	public TiredMod()
	{
		super("Tired", "Makes it look like you're about to fall asleep.");
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
		WConnection.sendPacket(
			new CPacketPlayer.Rotation(WMinecraft.getPlayer().rotationYaw,
				WMinecraft.getPlayer().ticksExisted % 100,
				WMinecraft.getPlayer().onGround));
	}
}
