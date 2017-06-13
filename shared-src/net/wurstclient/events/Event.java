/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.events;

import java.util.ArrayList;
import java.util.EventListener;

public abstract class Event<T extends EventListener>
{
	public abstract void fire(ArrayList<T> listeners);
	
	public abstract Class<T> getListenerType();
}
