/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.chat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.StringUtils;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.ChatInputListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;
import net.wurstclient.utils.ChatUtils;

@SearchTags({"mass tpa"})
@Mod.Bypasses
@Mod.DontSaveState
public final class MassTpaMod extends Mod
	implements UpdateListener, ChatInputListener
{
	private final Random random = new Random();
	private final ArrayList<String> players = new ArrayList<>();
	
	private int index;
	private int timer;
	
	public MassTpaMod()
	{
		super("MassTPA", "Sends a TPA request to all players.\n"
			+ "Stops if someone accepts.");
		setCategory(Category.CHAT);
	}
	
	@Override
	public void onEnable()
	{
		index = 0;
		timer = -1;
		players.clear();
		
		for(NetworkPlayerInfo info : WMinecraft.getConnection()
			.getPlayerInfoMap())
		{
			String name = info.getPlayerNameForReal();
			name = StringUtils.stripControlCodes(name);
			
			if(name.equals(WMinecraft.getPlayer().getName()))
				continue;
			
			players.add(name);
		}
		Collections.shuffle(players, random);
		
		wurst.events.add(ChatInputListener.class, this);
		wurst.events.add(UpdateListener.class, this);
		
		if(players.isEmpty())
		{
			ChatUtils.error("Couldn't find any players.");
			setEnabled(false);
		}
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(ChatInputListener.class, this);
		wurst.events.remove(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		if(timer > -1)
		{
			timer--;
			return;
		}
		
		if(index >= players.size())
			setEnabled(false);
		
		WMinecraft.getPlayer().sendChatMessage("/tpa " + players.get(index));
		index++;
		timer = 20;
	}
	
	@Override
	public void onReceivedMessage(ChatInputEvent event)
	{
		String message =
			event.getComponent().getUnformattedText().toLowerCase();
		if(message.startsWith("§c[§6wurst§c]"))
			return;
		
		if(message.contains("/help") || message.contains("permission"))
		{
			event.cancel();
			ChatUtils.error("This server doesn't have TPA.");
			setEnabled(false);
			
		}else if(message.contains("accepted") && message.contains("request")
			|| message.contains("akzeptiert") && message.contains("anfrage"))
		{
			event.cancel();
			ChatUtils.message("Someone accepted your TPA request. Stopping.");
			setEnabled(false);
		}
	}
}
