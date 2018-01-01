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

@HelpPage("Commands/gm")
public final class GmCmd extends Cmd
{
	public GmCmd()
	{
		super("gm", "Types \"/gamemode <args>\".\n"
			+ "Useful for servers that don't support /gm.", "<gamemode>");
	}
	
	@Override
	public void call(String[] args) throws CmdException
	{
		if(args.length != 1)
			throw new CmdSyntaxError();
		WMinecraft.getPlayer().sendChatMessage("/gamemode " + args[0]);
	}
}
