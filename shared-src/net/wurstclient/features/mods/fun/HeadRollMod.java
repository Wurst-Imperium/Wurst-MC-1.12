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
import net.wurstclient.compatibility.WMath;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;

@SearchTags({"head roll"})
@Mod.Bypasses(ghostMode = false, latestNCP = false, olderNCP = false)
public final class HeadRollMod extends Mod implements UpdateListener
{
	public HeadRollMod()
	{
		super("HeadRoll", "Makes you nod all the time.");
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
		float timer = WMinecraft.getPlayer().ticksExisted % 20 / 10F;
		float pitch = WMath.sin(timer * (float)Math.PI) * 90F;
		
		WConnection.sendPacket(
			new CPacketPlayer.Rotation(WMinecraft.getPlayer().rotationYaw,
				pitch, WMinecraft.getPlayer().onGround));
	}
}
