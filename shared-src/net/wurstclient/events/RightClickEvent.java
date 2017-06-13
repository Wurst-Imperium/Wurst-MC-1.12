/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.events;

import java.util.ArrayList;

import net.wurstclient.events.listeners.RightClickListener;

public class RightClickEvent extends CancellableEvent<RightClickListener>
{
	@Override
	public void fire(ArrayList<RightClickListener> listeners)
	{
		for(int i = 0; i < listeners.size(); i++)
		{
			listeners.get(i).onRightClick(this);
			if(isCancelled())
				break;
		}
	}
	
	@Override
	public Class<RightClickListener> getListenerType()
	{
		return RightClickListener.class;
	}
}
