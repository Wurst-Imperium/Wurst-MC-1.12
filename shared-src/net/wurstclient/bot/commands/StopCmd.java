/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.bot.commands;

import net.minecraft.client.Minecraft;

@Command.Info(help = "Stops Wurst-Bot.", name = "stop", syntax = {})
public class StopCmd extends Command
{
	@Override
	public void execute(String[] args) throws CmdError
	{
		if(args.length != 0)
			syntaxError();
		System.out.println("Stopping Wurst-Bot...");
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				System.out.println("Wurst-Bot stopped.");
			}
		}));
		Minecraft.getMinecraft().shutdown();
	}
}
