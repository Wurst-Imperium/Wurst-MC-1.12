/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.commands;

import net.wurstclient.features.Cmd;
import net.wurstclient.features.HelpPage;
import net.wurstclient.utils.ChatUtils;

@HelpPage("Commands/wms")
public final class WmsCmd extends Cmd
{
	public WmsCmd()
	{
		super("wms", "Enables/disables Wurst messages or sends a message.",
			"(on | off)", "echo <message>");
	}
	
	@Override
	public void call(String[] args) throws CmdException
	{
		if(args.length == 0)
			throw new CmdSyntaxError();
		if(args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("off"))
			ChatUtils.setEnabled(args[0].equalsIgnoreCase("on"));
		else if(args[0].equalsIgnoreCase("echo") && args.length >= 2)
		{
			String message = args[1];
			for(int i = 2; i < args.length; i++)
				message += " " + args[i];
			ChatUtils.cmd(message);
		}else
			throw new CmdSyntaxError();
	}
}
