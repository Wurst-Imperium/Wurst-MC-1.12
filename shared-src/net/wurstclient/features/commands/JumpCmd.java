/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.commands;

import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.ChatOutputEvent;
import net.wurstclient.features.Cmd;
import net.wurstclient.features.HelpPage;

@HelpPage("Commands/jump")
public final class JumpCmd extends Cmd
{
	public JumpCmd()
	{
		super("jump", "Makes you jump once.");
	}
	
	@Override
	public void execute(String[] args) throws CmdError
	{
		if(args.length != 0)
			syntaxError();
		if(!WMinecraft.getPlayer().onGround
			&& !wurst.mods.jetpackMod.isActive())
			error("Can't jump in mid-air.");
		WMinecraft.getPlayer().jump();
	}
	
	@Override
	public String getPrimaryAction()
	{
		return "Jump";
	}
	
	@Override
	public void doPrimaryAction()
	{
		wurst.commands.onSentMessage(new ChatOutputEvent(".jump", true));
	}
}
