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
import net.wurstclient.event.CancellableEvent;
import net.wurstclient.event.Listener;

public interface RenderBlockModelListener extends Listener
{
	public void onRenderBlockModel(RenderBlockModelEvent event);
	
	public static class RenderBlockModelEvent
		extends CancellableEvent<RenderBlockModelListener>
	{
		private final IBlockState state;
		
		public RenderBlockModelEvent(IBlockState state)
		{
			this.state = state;
		}
		
		public IBlockState getState()
		{
			return state;
		}
		
		@Override
		public void fire(ArrayList<RenderBlockModelListener> listeners)
		{
			for(RenderBlockModelListener listener : listeners)
			{
				listener.onRenderBlockModel(this);
				
				if(isCancelled())
					break;
			}
		}
		
		@Override
		public Class<RenderBlockModelListener> getListenerType()
		{
			return RenderBlockModelListener.class;
		}
	}
}
