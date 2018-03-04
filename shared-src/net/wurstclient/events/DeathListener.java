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

public interface DeathListener extends Listener
{
	public void onDeath();
	
	public static class DeathEvent extends Event<DeathListener>
	{
		public static final DeathEvent INSTANCE = new DeathEvent();
		
		@Override
		public void fire(ArrayList<DeathListener> listeners)
		{
			for(DeathListener listener : listeners)
				listener.onDeath();
		}
		
		@Override
		public Class<DeathListener> getListenerType()
		{
			return DeathListener.class;
		}
	}
}
