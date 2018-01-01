/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.compatibility;

import net.minecraft.network.Packet;

public final class WConnection
{
	public static void sendPacket(Packet packet)
	{
		WMinecraft.getConnection().sendPacket(packet);
	}
	
	public static void sendPacketBypass(Packet packet)
	{
		WMinecraft.getConnection().sendPacketBypass(packet);
	}
}
