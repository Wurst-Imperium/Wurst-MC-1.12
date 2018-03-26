/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.movement;

import java.util.ArrayDeque;

import net.minecraft.network.play.client.CPacketPlayer;
import net.wurstclient.compatibility.WConnection;
import net.wurstclient.events.PacketOutputListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Feature;
import net.wurstclient.features.Mod;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.utils.EntityFakePlayer;

@Mod.Bypasses
@Mod.DontSaveState
public final class BlinkMod extends Mod
	implements UpdateListener, PacketOutputListener
{
	private final SliderSetting limit = new SliderSetting("Limit",
		"Automatically restarts Blink once\n" + "the given number of packets\n"
			+ "have been suspended.\n\n" + "0 = no limit",
		0, 0, 500, 1, v -> v == 0 ? "disabled" : (int)v + "");
	
	private final ArrayDeque<CPacketPlayer> packets = new ArrayDeque<>();
	private EntityFakePlayer fakePlayer;
	
	public BlinkMod()
	{
		super("Blink", "Suspends all motion updates while enabled.");
		setCategory(Category.MOVEMENT);
		addSetting(limit);
	}
	
	@Override
	public String getRenderName()
	{
		if(limit.getValueI() == 0)
			return getName() + " [" + packets.size() + "]";
		else
			return getName() + " [" + packets.size() + "/" + limit.getValueI()
				+ "]";
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.commands.blinkCmd};
	}
	
	@Override
	public void onEnable()
	{
		fakePlayer = new EntityFakePlayer();
		
		wurst.events.add(UpdateListener.class, this);
		wurst.events.add(PacketOutputListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
		wurst.events.remove(PacketOutputListener.class, this);
		
		fakePlayer.despawn();
		packets.forEach(p -> WConnection.sendPacket(p));
		packets.clear();
	}
	
	@Override
	public void onUpdate()
	{
		if(limit.getValueI() == 0)
			return;
		
		if(packets.size() >= limit.getValueI())
		{
			setEnabled(false);
			setEnabled(true);
		}
	}
	
	@Override
	public void onSentPacket(PacketOutputEvent event)
	{
		if(!(event.getPacket() instanceof CPacketPlayer))
			return;
		
		event.cancel();
		
		CPacketPlayer packet = (CPacketPlayer)event.getPacket();
		CPacketPlayer prevPacket = packets.peekLast();
		
		if(prevPacket != null && packet.isOnGround() == prevPacket.isOnGround()
			&& packet.getYaw(-1) == prevPacket.getYaw(-1)
			&& packet.getPitch(-1) == prevPacket.getPitch(-1)
			&& packet.getX(-1) == prevPacket.getX(-1)
			&& packet.getY(-1) == prevPacket.getY(-1)
			&& packet.getZ(-1) == prevPacket.getZ(-1))
			return;
		
		packets.addLast(packet);
	}
	
	public void cancel()
	{
		packets.clear();
		fakePlayer.resetPlayerPosition();
		setEnabled(false);
	}
}
