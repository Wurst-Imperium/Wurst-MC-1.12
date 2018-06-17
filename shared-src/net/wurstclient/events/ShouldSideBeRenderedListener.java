/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.events;

import java.util.ArrayList;

import net.minecraft.block.state.IBlockState;
import net.wurstclient.event.Event;
import net.wurstclient.event.Listener;

public interface ShouldSideBeRenderedListener extends Listener
{
	public void onShouldSideBeRendered(ShouldSideBeRenderedEvent event);
	
	public static class ShouldSideBeRenderedEvent
		extends Event<ShouldSideBeRenderedListener>
	{
		private final IBlockState state;
		private boolean rendered;
		private final boolean normallyRendered;
		
		public ShouldSideBeRenderedEvent(IBlockState state, boolean rendered)
		{
			this.state = state;
			this.rendered = rendered;
			normallyRendered = rendered;
		}
		
		public IBlockState getState()
		{
			return state;
		}
		
		public boolean isRendered()
		{
			return rendered;
		}
		
		public void setRendered(boolean rendered)
		{
			this.rendered = rendered;
		}
		
		public boolean isNormallyRendered()
		{
			return normallyRendered;
		}
		
		@Override
		public void fire(ArrayList<ShouldSideBeRenderedListener> listeners)
		{
			for(ShouldSideBeRenderedListener listener : listeners)
				listener.onShouldSideBeRendered(this);
		}
		
		@Override
		public Class<ShouldSideBeRenderedListener> getListenerType()
		{
			return ShouldSideBeRenderedListener.class;
		}
	}
}
