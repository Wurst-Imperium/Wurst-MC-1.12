/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.commands;

import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.ChatInputListener;
import net.wurstclient.features.Cmd;
import net.wurstclient.features.HelpPage;
import net.wurstclient.utils.ChatUtils;

@HelpPage("Commands/annoy")
public final class AnnoyCmd extends Cmd implements ChatInputListener
{
	private boolean toggled;
	private String name;
	
	public AnnoyCmd()
	{
		super("annoy", "Annoys a player by repeating everything he says.",
			"[<player>]");
	}
	
	@Override
	public void call(String[] args) throws CmdException
	{
		toggled = !toggled;
		if(toggled)
		{
			if(args.length == 1)
			{
				name = args[0];
				ChatUtils.message("Now annoying " + name + ".");
				if(name.equals(WMinecraft.getPlayer().getName()))
					ChatUtils.warning("Annoying yourself is a bad idea!");
				wurst.events.add(ChatInputListener.class, this);
			}else
			{
				toggled = false;
				throw new CmdSyntaxError();
			}
		}else
		{
			wurst.events.remove(ChatInputListener.class, this);
			if(name != null)
			{
				ChatUtils.message("No longer annoying " + name + ".");
				name = null;
			}
		}
	}
	
	@Override
	public void onReceivedMessage(ChatInputEvent event)
	{
		String message = new String(event.getComponent().getUnformattedText());
		if(message.startsWith("§c[§6Wurst§c]§f "))
			return;
		if(message.startsWith("<" + name + ">") || message.contains(name + ">"))
		{
			String repeatMessage = message.substring(message.indexOf(">") + 1);
			WMinecraft.getPlayer().sendChatMessage(repeatMessage);
		}else if(message.contains("] " + name + ":")
			|| message.contains("]" + name + ":"))
		{
			String repeatMessage = message.substring(message.indexOf(":") + 1);
			WMinecraft.getPlayer().sendChatMessage(repeatMessage);
		}
	}
}
