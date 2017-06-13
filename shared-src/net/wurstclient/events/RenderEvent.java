/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.events;

import java.util.ArrayList;

import net.wurstclient.events.listeners.RenderListener;

public class RenderEvent extends Event<RenderListener>
{
	private final float partialTicks;
	
	public RenderEvent(float partialTicks)
	{
		this.partialTicks = partialTicks;
	}
	
	@Override
	public void fire(ArrayList<RenderListener> listeners)
	{
		for(int i = 0; i < listeners.size(); i++)
			listeners.get(i).onRender(partialTicks);
	}
	
	@Override
	public Class<RenderListener> getListenerType()
	{
		return RenderListener.class;
	}
}
