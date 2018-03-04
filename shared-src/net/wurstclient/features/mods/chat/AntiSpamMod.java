/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.chat;

import java.util.Iterator;

import net.minecraft.client.gui.ChatLine;
import net.wurstclient.events.ChatInputListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;
import net.wurstclient.utils.MiscUtils;

@SearchTags({"NoSpam", "ChatFilter", "anti spam", "no spam", "chat filter"})
@Mod.Bypasses
public final class AntiSpamMod extends Mod implements ChatInputListener
{
	public AntiSpamMod()
	{
		super("AntiSpam",
			"Blocks chat spam by adding a counter to repeated messages.");
		setCategory(Category.CHAT);
	}
	
	@Override
	public void onEnable()
	{
		wurst.events.add(ChatInputListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(ChatInputListener.class, this);
	}
	
	@Override
	public void onReceivedMessage(ChatInputEvent event)
	{
		// check if chat history is empty
		if(event.getChatLines().isEmpty())
			return;
		
		String newLine = event.getComponent().getUnformattedText();
		int spamCounter = 1;
		
		// count and remove duplicates of the new line
		for(Iterator<ChatLine> itr = event.getChatLines().iterator(); itr
			.hasNext();)
		{
			// find old lines that start with the new one
			String line = itr.next().getChatComponent().getUnformattedText();
			if(!line.startsWith(newLine))
				continue;
			
			// if the old line equals the new one, add 1 to the counter
			if(line.length() == newLine.length())
				spamCounter++;
			else
			{
				// if the old line equals the new one plus added text, check if
				// that text is a spam counter
				
				// first check if it matches " [x.*]"
				String addedText = line.substring(newLine.length());
				if(!addedText.startsWith(" [x") || !addedText.endsWith("]"))
					continue;
				
				// then check if the counter value is a valid number
				String oldSpamCounter =
					addedText.substring(3, addedText.length() - 1);
				if(!MiscUtils.isInteger(oldSpamCounter))
					continue;
				
				// if the old counter is valid, add its value to the new counter
				spamCounter += Integer.parseInt(oldSpamCounter);
			}
			
			// remove the old line
			itr.remove();
		}
		
		// if the new line exists more than once, add a spam counter to it
		if(spamCounter > 1)
			event.getComponent().appendText(" [x" + spamCounter + "]");
	}
}
