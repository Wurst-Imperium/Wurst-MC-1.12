/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.commands;

import net.minecraft.network.play.client.CPacketPlayer;
import net.wurstclient.compatibility.WConnection;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.features.Cmd;
import net.wurstclient.features.HelpPage;
import net.wurstclient.utils.MiscUtils;

@HelpPage("Commands/damage")
public final class DamageCmd extends Cmd
{
	public DamageCmd()
	{
		super("damage", "Applies the given amount of damage.", "<amount>");
	}
	
	@Override
	public void execute(String[] args) throws CmdError
	{
		if(args.length == 0)
			syntaxError();
		
		// check amount
		if(!MiscUtils.isInteger(args[0]))
			syntaxError("Amount must be a number.");
		int dmg = Integer.parseInt(args[0]);
		if(dmg < 1)
			error("Amount must be at least 1.");
		if(dmg > 40)
			error("Amount must be at most 20.");
		
		// check gamemode
		if(WMinecraft.getPlayer().capabilities.isCreativeMode)
			error("Cannot damage in creative mode.");
		
		double posX = WMinecraft.getPlayer().posX;
		double posY = WMinecraft.getPlayer().posY;
		double posZ = WMinecraft.getPlayer().posZ;
		
		// apply damage
		for(int i = 0; i < 80 + 20 * (dmg - 1D); ++i)
		{
			WConnection.sendPacket(
				new CPacketPlayer.Position(posX, posY + 0.049D, posZ, false));
			WConnection.sendPacket(
				new CPacketPlayer.Position(posX, posY, posZ, false));
		}
		WConnection
			.sendPacket(new CPacketPlayer.Position(posX, posY, posZ, true));
	}
}
