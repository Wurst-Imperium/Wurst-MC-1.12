/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.commands;

import net.minecraft.util.math.BlockPos;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.features.Cmd;
import net.wurstclient.features.HelpPage;

@HelpPage("Commands/tp")
public final class TpCmd extends Cmd
{
	public TpCmd()
	{
		super("tp",
			"Teleports you up to 100 blocks away.\nOnly works on vanilla servers!",
			"<x> <y> <z>", "<entity>");
	}
	
	@Override
	public void call(String[] args) throws CmdException
	{
		BlockPos pos = argsToPos(args);
		WMinecraft.getPlayer().setPosition(pos.getX() + 0.5, pos.getY(),
			pos.getZ() + 0.5);
	}
}
