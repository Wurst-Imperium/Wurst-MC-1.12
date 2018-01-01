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

@HelpPage("Commands/rv")
public final class RvCmd extends Cmd
{
	public RvCmd()
	{
		super("rv", "Toggles RemoteView or makes it target a specific entity.",
			"[<player>]");
	}
	
	@Override
	public void call(String[] args) throws CmdException
	{
		if(args.length == 0)
		{
			wurst.mods.remoteViewMod.onToggledByCommand(null);
			return;
		}else if(args.length == 1)
			wurst.mods.remoteViewMod.onToggledByCommand(args[0]);
		else
			throw new CmdSyntaxError("Too many arguments.");
	}
}
