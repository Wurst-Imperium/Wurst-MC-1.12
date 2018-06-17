/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.events;

import java.util.ArrayList;

import net.minecraft.tileentity.TileEntity;
import net.wurstclient.event.CancellableEvent;
import net.wurstclient.event.Listener;

public interface RenderTileEntityListener extends Listener
{
	public void onRenderTileEntity(RenderTileEntityEvent event);
	
	public static class RenderTileEntityEvent
		extends CancellableEvent<RenderTileEntityListener>
	{
		private final TileEntity tileEntity;
		
		public RenderTileEntityEvent(TileEntity tileEntity)
		{
			this.tileEntity = tileEntity;
		}
		
		public TileEntity getTileEntity()
		{
			return tileEntity;
		}
		
		@Override
		public void fire(ArrayList<RenderTileEntityListener> listeners)
		{
			for(RenderTileEntityListener listener : listeners)
			{
				listener.onRenderTileEntity(this);
				
				if(isCancelled())
					break;
			}
		}
		
		@Override
		public Class<RenderTileEntityListener> getListenerType()
		{
			return RenderTileEntityListener.class;
		}
	}
}
