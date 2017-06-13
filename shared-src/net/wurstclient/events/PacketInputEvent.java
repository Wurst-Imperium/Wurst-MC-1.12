/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.events;

import java.util.ArrayList;

import net.minecraft.network.Packet;
import net.wurstclient.events.listeners.PacketInputListener;

public class PacketInputEvent extends CancellableEvent<PacketInputListener>
{
	private Packet packet;
	
	public PacketInputEvent(Packet packet)
	{
		this.packet = packet;
	}
	
	public Packet getPacket()
	{
		return packet;
	}
	
	public void setPacket(Packet packet)
	{
		this.packet = packet;
	}
	
	@Override
	public void fire(ArrayList<PacketInputListener> listeners)
	{
		for(int i = 0; i < listeners.size(); i++)
		{
			listeners.get(i).onReceivedPacket(this);
			if(isCancelled())
				break;
		}
	}
	
	@Override
	public Class<PacketInputListener> getListenerType()
	{
		return PacketInputListener.class;
	}
}
