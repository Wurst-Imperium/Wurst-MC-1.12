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

public interface RenderListener extends Listener
{
	public void onRender(float partialTicks);
	
	public static class RenderEvent extends Event<RenderListener>
	{
		private final float partialTicks;
		
		public RenderEvent(float partialTicks)
		{
			this.partialTicks = partialTicks;
		}
		
		@Override
		public void fire(ArrayList<RenderListener> listeners)
		{
			for(RenderListener listener : listeners)
				listener.onRender(partialTicks);
		}
		
		@Override
		public Class<RenderListener> getListenerType()
		{
			return RenderListener.class;
		}
	}
}
