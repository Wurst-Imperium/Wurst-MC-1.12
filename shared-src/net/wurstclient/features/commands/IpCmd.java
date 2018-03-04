/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.commands;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;

import net.wurstclient.events.ChatOutputListener.ChatOutputEvent;
import net.wurstclient.features.Cmd;
import net.wurstclient.features.HelpPage;
import net.wurstclient.hooks.ServerHook;
import net.wurstclient.utils.ChatUtils;

@HelpPage("Commands/ip")
public final class IpCmd extends Cmd
{
	public IpCmd()
	{
		super("ip",
			"Shows the IP of the server you are currently playing on or copies it to the clipboard.",
			"[copy]");
	}
	
	@Override
	public void call(String[] args) throws CmdException
	{
		if(args.length == 0)
			ChatUtils.message("IP: " + ServerHook.getCurrentServerIP());
		else if(args[0].toLowerCase().equals("copy"))
		{
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
				new StringSelection(ServerHook.getCurrentServerIP()), null);
			ChatUtils.message("IP copied to clipboard.");
		}else
			throw new CmdSyntaxError();
	}
	
	@Override
	public String getPrimaryAction()
	{
		return "Get IP";
	}
	
	@Override
	public void doPrimaryAction()
	{
		wurst.commands.onSentMessage(new ChatOutputEvent(".ip", true));
	}
}
