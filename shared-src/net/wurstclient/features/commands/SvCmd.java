/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.commands;

import net.wurstclient.events.ChatOutputListener.ChatOutputEvent;
import net.wurstclient.features.Cmd;
import net.wurstclient.features.HelpPage;
import net.wurstclient.hooks.ServerHook;
import net.wurstclient.utils.ChatUtils;

@HelpPage("Commands/sv")
public final class SvCmd extends Cmd
{
	public SvCmd()
	{
		super("sv",
			"Shows the version of the server you are currently playing on.");
	}
	
	@Override
	public void call(String[] args) throws CmdException
	{
		if(args.length != 0)
			throw new CmdSyntaxError();
		if(mc.isSingleplayer())
			throw new CmdError("Can't check server version in singleplayer.");
		ChatUtils.message(
			"Server version: " + ServerHook.getLastServerData().gameVersion);
	}
	
	@Override
	public String getPrimaryAction()
	{
		return "Get Server Version";
	}
	
	@Override
	public void doPrimaryAction()
	{
		wurst.commands.onSentMessage(new ChatOutputEvent(".sv", true));
	}
}
