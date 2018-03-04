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

public interface KeyPressListener extends Listener
{
	public void onKeyPress(KeyPressEvent event);
	
	public static class KeyPressEvent extends Event<KeyPressListener>
	{
		private final int keyCode;
		private final String keyName;
		
		public KeyPressEvent(int keyCode, String keyName)
		{
			this.keyCode = keyCode;
			this.keyName = keyName;
		}
		
		@Override
		public void fire(ArrayList<KeyPressListener> listeners)
		{
			for(KeyPressListener listener : listeners)
				listener.onKeyPress(this);
		}
		
		@Override
		public Class<KeyPressListener> getListenerType()
		{
			return KeyPressListener.class;
		}
		
		public int getKeyCode()
		{
			return keyCode;
		}
		
		public String getKeyName()
		{
			return keyName;
		}
	}
}
