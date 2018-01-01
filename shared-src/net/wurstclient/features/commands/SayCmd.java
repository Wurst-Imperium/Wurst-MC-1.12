/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.commands;

import net.minecraft.network.play.client.CPacketChatMessage;
import net.wurstclient.compatibility.WConnection;
import net.wurstclient.features.Cmd;
import net.wurstclient.features.HelpPage;
import net.wurstclient.features.SearchTags;

@SearchTags({".legit", "dots in chat", "command bypass", "prefix"})
@HelpPage("Commands/say")
public final class SayCmd extends Cmd
{
	public SayCmd()
	{
		super("say",
			"Sends a chat message, even if the message starts with a dot.",
			"<message>");
	}
	
	@Override
	public void call(String[] args) throws CmdException
	{
		if(args.length > 0)
		{
			String message = args[0];
			for(int i = 1; i < args.length; i++)
				message += " " + args[i];
			WConnection.sendPacket(new CPacketChatMessage(message));
		}else
			throw new CmdSyntaxError("Message required.");
	}
}
