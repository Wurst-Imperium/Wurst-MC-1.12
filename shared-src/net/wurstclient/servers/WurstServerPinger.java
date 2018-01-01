/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.servers;

import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.ServerPinger;

public class WurstServerPinger
{
	private static final AtomicInteger threadNumber = new AtomicInteger(0);
	public static final Logger logger = LogManager.getLogger();
	public ServerData server;
	private boolean done = false;
	private boolean failed = false;
	
	public void ping(final String ip)
	{
		ping(ip, 25565);
	}
	
	public void ping(final String ip, final int port)
	{
		server = new ServerData("", ip + ":" + port, false);
		new Thread("Wurst Server Connector #" + threadNumber.incrementAndGet())
		{
			@Override
			public void run()
			{
				ServerPinger pinger = new ServerPinger();
				try
				{
					logger.info("Pinging " + ip + ":" + port + "...");
					pinger.ping(server);
					logger.info("Ping successful: " + ip + ":" + port);
				}catch(UnknownHostException e)
				{
					logger.info("Unknown host: " + ip + ":" + port);
					failed = true;
				}catch(Exception e2)
				{
					logger.info("Ping failed: " + ip + ":" + port);
					failed = true;
				}
				pinger.clearPendingNetworks();
				done = true;
			}
		}.start();
	}
	
	public boolean isStillPinging()
	{
		return !done;
	}
	
	public boolean isWorking()
	{
		return !failed;
	}
	
	public boolean isOtherVersion()
	{
		return server.version != 47;
	}
}
