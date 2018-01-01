/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.commands;

import net.wurstclient.compatibility.WChat;
import net.wurstclient.features.Cmd;
import net.wurstclient.features.HelpPage;

@HelpPage("Commands/clear")
public final class ClearCmd extends Cmd
{
	public ClearCmd()
	{
		super("clear", "Clears the chat completely.");
	}
	
	@Override
	public void call(String[] args) throws CmdException
	{
		if(args.length == 0)
			WChat.clearMessages();
		else
			throw new CmdSyntaxError();
	}
}
