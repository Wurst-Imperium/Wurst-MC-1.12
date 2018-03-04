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

@HelpPage("Commands/nothing")
public final class NothingCmd extends Cmd
{
	public NothingCmd()
	{
		super("nothing", "Does nothing. Useful for scripting.");
	}
	
	@Override
	public void call(String[] args) throws CmdError
	{
		
	}
	
	@Override
	public String getPrimaryAction()
	{
		return "Do Nothing";
	}
	
	@Override
	public void doPrimaryAction()
	{
		wurst.commands.onSentMessage(new ChatOutputEvent(".nothing", true));
	}
}
