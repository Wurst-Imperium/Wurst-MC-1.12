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

@HelpPage("Commands/blink")
public final class BlinkCmd extends Cmd
{
	public BlinkCmd()
	{
		super("blink", "Enables, disables or cancels Blink.",
			"[(on|off|cancel)]");
	}
	
	@Override
	public void call(String[] args) throws CmdException
	{
		if(args.length > 1)
			throw new CmdSyntaxError();
		if(args.length == 0)
			wurst.mods.blinkMod.toggle();
		else if(args[0].equalsIgnoreCase("on"))
		{
			if(!wurst.mods.blinkMod.isEnabled())
				wurst.mods.blinkMod.setEnabled(true);
		}else if(args[0].equalsIgnoreCase("off"))
			wurst.mods.blinkMod.setEnabled(false);
		else if(args[0].equalsIgnoreCase("cancel"))
		{
			if(wurst.mods.blinkMod.isEnabled())
				wurst.mods.blinkMod.cancel();
		}else
			throw new CmdSyntaxError();
	}
}
