/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.compatibility;

import java.io.IOException;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public final class SPacketSomethingNew implements Packet<INetHandlerPlayClient>
{
	@Override
	public void readPacketData(PacketBuffer buf) throws IOException
	{
		buf.readByte();
		buf.readVarIntFromBuffer();
	}
	
	@Override
	public void writePacketData(PacketBuffer buf) throws IOException
	{
		buf.writeByte(0);
		buf.writeVarIntToBuffer(0);
	}
	
	@Override
	public void processPacket(INetHandlerPlayClient handler)
	{
		
	}
}
