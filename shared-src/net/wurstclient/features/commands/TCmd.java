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
import net.wurstclient.features.Mod;

@HelpPage("Commands/t")
public final class TCmd extends Cmd
{
	public TCmd()
	{
		super("t", "Toggles a mod.", "<mod> [(on|off)]");
	}
	
	@Override
	public void call(String[] args) throws CmdException
	{
		int mode = -1;
		if(args.length == 1)
			mode = 0;
		else if(args.length == 2 && args[1].equalsIgnoreCase("on"))
			mode = 1;
		else if(args.length == 2 && args[1].equalsIgnoreCase("off"))
			mode = 2;
		else
			throw new CmdSyntaxError();
		Mod mod = wurst.mods.getModByName(args[0]);
		if(mod == null)
			throw new CmdError("Could not find mod \"" + args[0] + "\".");
		if(mode == 0)
			mod.toggle();
		else if(mode == 1 && !mod.isEnabled())
			mod.setEnabled(true);
		else if(mode == 2 && mod.isEnabled())
			mod.setEnabled(false);
	}
}
