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

public interface LeftClickListener extends Listener
{
	public void onLeftClick(LeftClickEvent event);
	
	public static class LeftClickEvent
		extends CancellableEvent<LeftClickListener>
	{
		@Override
		public void fire(ArrayList<LeftClickListener> listeners)
		{
			for(LeftClickListener listener : listeners)
			{
				listener.onLeftClick(this);
				
				if(isCancelled())
					break;
			}
		}
		
		@Override
		public Class<LeftClickListener> getListenerType()
		{
			return LeftClickListener.class;
		}
	}
}
