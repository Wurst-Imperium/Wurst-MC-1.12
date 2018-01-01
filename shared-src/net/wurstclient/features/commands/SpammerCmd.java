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
import net.wurstclient.features.mods.chat.SpammerMod;
import net.wurstclient.spam.SpamProcessor;
import net.wurstclient.utils.ChatUtils;
import net.wurstclient.utils.MiscUtils;

@HelpPage("Commands/spammer")
public final class SpammerCmd extends Cmd
{
	public SpammerCmd()
	{
		super("spammer",
			"Changes the delay of Spammer or spams spam from a file.",
			"delay <delay_in_ms>", "spam <file>");
	}
	
	@Override
	public void call(String[] args) throws CmdException
	{
		if(args.length != 2)
			throw new CmdSyntaxError();
		if(args[0].equalsIgnoreCase("delay"))
		{
			if(!MiscUtils.isInteger(args[1]))
				throw new CmdSyntaxError();
			int newDelay = Integer.parseInt(args[1]);
			if(newDelay % 50 > 0)
				newDelay = newDelay - newDelay % 50;
			wurst.options.spamDelay = newDelay;
			SpammerMod.updateDelaySpinner();
			ChatUtils.message("Spammer delay set to " + newDelay + "ms.");
		}else if(args[0].equalsIgnoreCase("spam"))
			if(!SpamProcessor.runSpam(args[1]))
				ChatUtils.error("File does not exist.");
	}
}
