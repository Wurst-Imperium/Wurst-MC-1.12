/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.bot.commands;

import net.wurstclient.utils.MiscUtils;

@Command.Info(
	help = "Changes the proxy used for server connections. Must be a SOCKS proxy.",
	name = "proxy",
	syntax = {"<ip>:<port>", "none"})
public class ProxyCmd extends Command
{
	@Override
	public void execute(String[] args) throws CmdError
	{
		if(args.length < 1 || args.length > 2)
			syntaxError();
		if(args[0].contains(":"))
		{
			String ip = args[0].split(":")[0];
			String portSring = args[0].split(":")[1];
			if(!MiscUtils.isInteger(portSring))
				syntaxError("Invalid port: " + portSring);
			try
			{
				System.setProperty("socksProxyHost", ip);
				System.setProperty("socksProxyPort", portSring);
			}catch(Exception e)
			{
				error(e.getMessage());
			}
		}else if(args[0].equalsIgnoreCase("none"))
		{
			System.setProperty("socksProxyHost", "");
			System.setProperty("socksProxyPort", "");
		}else
			syntaxError("Not a proxy: " + args[0]);
		System.out.println("Proxy set to " + args[0]);
	}
}
