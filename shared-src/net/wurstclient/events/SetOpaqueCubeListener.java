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

public interface SetOpaqueCubeListener extends Listener
{
	public void onSetOpaqueCube(SetOpaqueCubeEvent event);
	
	public static class SetOpaqueCubeEvent
		extends CancellableEvent<SetOpaqueCubeListener>
	{
		@Override
		public void fire(ArrayList<SetOpaqueCubeListener> listeners)
		{
			for(SetOpaqueCubeListener listener : listeners)
			{
				listener.onSetOpaqueCube(this);
				
				if(isCancelled())
					break;
			}
		}
		
		@Override
		public Class<SetOpaqueCubeListener> getListenerType()
		{
			return SetOpaqueCubeListener.class;
		}
	}
}
