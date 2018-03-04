/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.events;

import java.util.ArrayList;

import net.wurstclient.event.Event;
import net.wurstclient.event.Listener;

public interface PostUpdateListener extends Listener
{
	public void afterUpdate();
	
	public static class PostUpdateEvent extends Event<PostUpdateListener>
	{
		public static final PostUpdateEvent INSTANCE = new PostUpdateEvent();
		
		@Override
		public void fire(ArrayList<PostUpdateListener> listeners)
		{
			for(PostUpdateListener listener : listeners)
				listener.afterUpdate();
		}
		
		@Override
		public Class<PostUpdateListener> getListenerType()
		{
			return PostUpdateListener.class;
		}
	}
}
