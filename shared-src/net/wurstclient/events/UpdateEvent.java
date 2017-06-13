/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.events;

import java.util.ArrayList;

import net.wurstclient.events.listeners.UpdateListener;

public class UpdateEvent extends Event<UpdateListener>
{
	public static final UpdateEvent INSTANCE = new UpdateEvent();
	
	@Override
	public void fire(ArrayList<UpdateListener> listeners)
	{
		for(int i = 0; i < listeners.size(); i++)
			listeners.get(i).onUpdate();
	}
	
	@Override
	public Class<UpdateListener> getListenerType()
	{
		return UpdateListener.class;
	}
}
