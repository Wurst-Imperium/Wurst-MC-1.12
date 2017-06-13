/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.commands;

import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.features.Cmd;
import net.wurstclient.features.HelpPage;
import net.wurstclient.utils.EntityUtils.TargetSettings;

@HelpPage("Commands/tp")
public final class TpCmd extends Cmd
{
	private TargetSettings targetSettings = new TargetSettings()
	{
		@Override
		public boolean targetFriends()
		{
			return true;
		}
		
		@Override
		public boolean targetBehindWalls()
		{
			return true;
		}
	};
	
	public TpCmd()
	{
		super("tp",
			"Teleports you up to 100 blocks away.\nOnly works on vanilla servers!",
			"<x> <y> <z>", "<entity>");
	}
	
	@Override
	public void execute(String[] args) throws CmdError
	{
		int[] pos = argsToPos(targetSettings, args);
		WMinecraft.getPlayer().setPosition(pos[0], pos[1], pos[2]);
	}
}
