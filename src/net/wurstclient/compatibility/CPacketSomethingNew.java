/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
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

public final class CPacketSomethingNew implements Packet<INetHandlerPlayClient>
{
	@Override
	public void readPacketData(PacketBuffer buf) throws IOException
	{
		buf.readByte();
		buf.readVarIntFromBuffer();
		buf.readBoolean();
	}
	
	@Override
	public void writePacketData(PacketBuffer buf) throws IOException
	{
		buf.writeByte(0);
		buf.writeVarIntToBuffer(0);
		buf.writeBoolean(false);
	}
	
	@Override
	public void processPacket(INetHandlerPlayClient handler)
	{
		
	}
}
