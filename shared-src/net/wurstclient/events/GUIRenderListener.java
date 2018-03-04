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

public interface GUIRenderListener extends Listener
{
	public void onRenderGUI();
	
	public static class GUIRenderEvent extends Event<GUIRenderListener>
	{
		public static final GUIRenderEvent INSTANCE = new GUIRenderEvent();
		
		@Override
		public void fire(ArrayList<GUIRenderListener> listeners)
		{
			for(GUIRenderListener listener : listeners)
				listener.onRenderGUI();
		}
		
		@Override
		public Class<GUIRenderListener> getListenerType()
		{
			return GUIRenderListener.class;
		}
	}
}
