/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.events;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.ChatLine;
import net.minecraft.util.text.ITextComponent;
import net.wurstclient.event.CancellableEvent;
import net.wurstclient.event.Listener;

public interface ChatInputListener extends Listener
{
	public void onReceivedMessage(ChatInputEvent event);
	
	public static class ChatInputEvent
		extends CancellableEvent<ChatInputListener>
	{
		private ITextComponent component;
		private List<ChatLine> chatLines;
		
		public ChatInputEvent(ITextComponent component,
			List<ChatLine> chatLines)
		{
			this.component = component;
			this.chatLines = chatLines;
		}
		
		public ITextComponent getComponent()
		{
			return component;
		}
		
		public void setComponent(ITextComponent component)
		{
			this.component = component;
		}
		
		public List<ChatLine> getChatLines()
		{
			return chatLines;
		}
		
		@Override
		public void fire(ArrayList<ChatInputListener> listeners)
		{
			for(ChatInputListener listener : listeners)
			{
				listener.onReceivedMessage(this);
				
				if(isCancelled())
					break;
			}
		}
		
		@Override
		public Class<ChatInputListener> getListenerType()
		{
			return ChatInputListener.class;
		}
	}
}
