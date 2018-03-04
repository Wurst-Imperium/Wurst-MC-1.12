/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.chat;

import net.wurstclient.events.ChatOutputListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;

@SearchTags({"fancy chat"})
@Mod.Bypasses(ghostMode = false, mineplex = false)
public final class FancyChatMod extends Mod implements ChatOutputListener
{
	private final String blacklist = "(){}[]|";
	
	public FancyChatMod()
	{
		super("FancyChat",
			"Replaces ASCII characters in sent chat messages with fancier unicode characters. Can be\n"
				+ "used to bypass curse word filters on some servers. Does not work on servers that block\n"
				+ "unicode characters.");
		setCategory(Category.CHAT);
	}
	
	@Override
	public void onEnable()
	{
		wurst.events.add(ChatOutputListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(ChatOutputListener.class, this);
	}
	
	@Override
	public void onSentMessage(ChatOutputEvent event)
	{
		if(event.getMessage().startsWith("/")
			|| event.getMessage().startsWith("."))
			return;
		
		String out = "";
		
		for(char c : event.getMessage().toCharArray())
			if(c >= 0x21 && c <= 0x80
				&& !blacklist.contains(Character.toString(c)))
				out += new String(Character.toChars(c + 0xfee0));
			else
				out += c;
			
		event.setMessage(out);
	}
}
