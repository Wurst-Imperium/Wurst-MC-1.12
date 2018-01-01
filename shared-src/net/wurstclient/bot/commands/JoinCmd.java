/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.bot.commands;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.wurstclient.gui.main.GuiWurstMainMenu;

@Command.Info(help = "Joins a server.", name = "join", syntax = {"<ip>"})
public final class JoinCmd extends Command
{
	@Override
	public void execute(String[] args) throws CmdError
	{
		if(args.length != 1)
			syntaxError();
		
		Minecraft mc = Minecraft.getMinecraft();
		
		mc.addScheduledTask(() -> {
			
			mc.displayGuiScreen(new GuiConnecting(new GuiWurstMainMenu(), mc,
				new ServerData("", args[0], false)));
			
			System.out.println(
				"Joined " + args[0] + " as " + mc.session.getUsername());
		});
	}
}
