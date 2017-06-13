/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.events;

import java.util.ArrayList;

import net.wurstclient.events.listeners.PostUpdateListener;

public class PostUpdateEvent extends Event<PostUpdateListener>
{
	public static final PostUpdateEvent INSTANCE = new PostUpdateEvent();
	
	@Override
	public void fire(ArrayList<PostUpdateListener> listeners)
	{
		for(int i = 0; i < listeners.size(); i++)
			listeners.get(i).afterUpdate();
	}
	
	@Override
	public Class<PostUpdateListener> getListenerType()
	{
		return PostUpdateListener.class;
	}
}
