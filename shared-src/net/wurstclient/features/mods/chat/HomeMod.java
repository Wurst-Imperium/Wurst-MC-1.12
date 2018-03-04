/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.chat;

import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.ChatInputListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Mod;
import net.wurstclient.utils.ChatUtils;

@Mod.Bypasses
public final class HomeMod extends Mod
	implements UpdateListener, ChatInputListener
{
	private int disableTimer;
	
	public HomeMod()
	{
		super("/home", "Types §l/home§r instantly.");
		setCategory(Category.CHAT);
	}
	
	@Override
	public void onEnable()
	{
		disableTimer = 0;
		wurst.events.add(ChatInputListener.class, this);
		wurst.events.add(UpdateListener.class, this);
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
		if(disableTimer >= 4)
			setEnabled(false);
		else if(disableTimer == 0)
			WMinecraft.getPlayer().sendChatMessage("/home");
		disableTimer++;
	}
	
	@Override
	public void onReceivedMessage(ChatInputEvent event)
	{
		String message = event.getComponent().getUnformattedText();
		if(message.startsWith("§c[§6Wurst§c]§f "))
			return;
		if(message.toLowerCase().contains("/help")
			|| message.toLowerCase().contains("permission"))
		{
			event.cancel();
			ChatUtils.error("This server doesn't have /home.");
		}
	}
}
