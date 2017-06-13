/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.events;

import java.util.ArrayList;

import net.wurstclient.events.listeners.DeathListener;

public class DeathEvent extends Event<DeathListener>
{
	public static final DeathEvent INSTANCE = new DeathEvent();
	
	@Override
	public void fire(ArrayList<DeathListener> listeners)
	{
		for(int i = 0; i < listeners.size(); i++)
			listeners.get(i).onDeath();
	}
	
	@Override
	public Class<DeathListener> getListenerType()
	{
		return DeathListener.class;
	}
}
