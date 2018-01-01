/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.bot.commands;

import net.minecraft.client.Minecraft;
import net.wurstclient.altmanager.LoginManager;

@Command.Info(help = "Logs you in with a premium or cracked account.",
	name = "login",
	syntax = {"<email> <password>", "<name>"})
public class LoginCmd extends Command
{
	@Override
	public void execute(String[] args) throws CmdError
	{
		if(args.length < 1 || args.length > 2)
			syntaxError();
		if(args.length == 1)
		{
			LoginManager.changeCrackedName(args[0]);
			System.out.println("Changed name to \"" + args[0] + "\".");
		}else
		{
			String error = LoginManager.login(args[0], args[1]);
			if(error.isEmpty())
				System.out.println("Logged in as "
					+ Minecraft.getMinecraft().session.getUsername());
			else
				error(error);
		}
	}
}
