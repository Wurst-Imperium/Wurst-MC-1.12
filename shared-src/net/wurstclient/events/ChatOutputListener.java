/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.events;

import java.util.ArrayList;

import net.wurstclient.event.CancellableEvent;
import net.wurstclient.event.Listener;

public interface ChatOutputListener extends Listener
{
	public void onSentMessage(ChatOutputEvent event);
	
	public static class ChatOutputEvent
		extends CancellableEvent<ChatOutputListener>
	{
		private String message;
		private boolean automatic;
		
		public ChatOutputEvent(String message, boolean automatic)
		{
			this.message = message;
			this.automatic = automatic;
		}
		
		public String getMessage()
		{
			return message;
		}
		
		public void setMessage(String message)
		{
			this.message = message;
		}
		
		public boolean isAutomatic()
		{
			return automatic;
		}
		
		@Override
		public void fire(ArrayList<ChatOutputListener> listeners)
		{
			for(ChatOutputListener listener : listeners)
			{
				listener.onSentMessage(this);
				
				if(isCancelled())
					break;
			}
		}
		
		@Override
		public Class<ChatOutputListener> getListenerType()
		{
			return ChatOutputListener.class;
		}
	}
}
