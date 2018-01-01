/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.movement;

import java.util.ArrayList;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.wurstclient.compatibility.WConnection;
import net.wurstclient.events.PacketOutputEvent;
import net.wurstclient.events.listeners.PacketOutputListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Feature;
import net.wurstclient.features.Mod;
import net.wurstclient.utils.EntityFakePlayer;

@Mod.Bypasses
@Mod.DontSaveState
public final class BlinkMod extends Mod implements PacketOutputListener
{
	private final ArrayList<Packet> packets = new ArrayList<>();
	private EntityFakePlayer fakePlayer;
	private int blinkTime;
	
	public BlinkMod()
	{
		super("Blink", "Suspends all motion updates while enabled.\n"
			+ "Can be used for teleportation, instant picking up of items and more.");
		setCategory(Category.MOVEMENT);
	}
	
	@Override
	public String getRenderName()
	{
		return "Blink [" + blinkTime + "ms]";
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.commands.blinkCmd};
	}
	
	@Override
	public void onEnable()
	{
		// reset timer
		blinkTime = 0;
		
		fakePlayer = new EntityFakePlayer();
		
		// add listener
		wurst.events.add(PacketOutputListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		// remove listener
		wurst.events.remove(PacketOutputListener.class, this);
		
		// send & delete saved packets
		for(Packet packet : packets)
			WConnection.sendPacket(packet);
		packets.clear();
		
		fakePlayer.despawn();
	}
	
	@Override
	public void onSentPacket(PacketOutputEvent event)
	{
		Packet packet = event.getPacket();
		
		// check for player packets
		if(!(packet instanceof CPacketPlayer))
			return;
		
		// cancel player packets
		event.cancel();
		
		// check for movement packets
		if(!(packet instanceof CPacketPlayer.Position)
			&& !(packet instanceof CPacketPlayer.PositionRotation))
			return;
		
		// save movement packets
		packets.add(packet);
		blinkTime += 50;
	}
	
	public void cancel()
	{
		// delete saved packets
		packets.clear();
		
		fakePlayer.resetPlayerPosition();
		
		setEnabled(false);
	}
}
