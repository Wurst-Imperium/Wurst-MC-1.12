/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.commands;

import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.features.Cmd;
import net.wurstclient.features.HelpPage;
import net.wurstclient.utils.MiscUtils;

@HelpPage("Commands/vclip")
public final class VClipCmd extends Cmd
{
	public VClipCmd()
	{
		super("vclip",
			"Teleports you up/down. Can glitch you through floors & ceilings.\n"
				+ "The maximum distance is 100 blocks on vanilla servers and 10 blocks on Bukkit servers.",
			"<height>");
	}
	
	@Override
	public void call(String[] args) throws CmdException
	{
		if(args.length != 1)
			throw new CmdSyntaxError();
		if(MiscUtils.isInteger(args[0]))
			WMinecraft.getPlayer().setPosition(WMinecraft.getPlayer().posX,
				WMinecraft.getPlayer().posY + Integer.valueOf(args[0]),
				WMinecraft.getPlayer().posZ);
		else
			throw new CmdSyntaxError();
	}
}
