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

public interface GetAmbientOcclusionLightValueListener extends Listener
{
	public void onGetAmbientOcclusionLightValue(
		GetAmbientOcclusionLightValueEvent event);
	
	public static class GetAmbientOcclusionLightValueEvent
		extends Event<GetAmbientOcclusionLightValueListener>
	{
		private final IBlockState state;
		private float lightValue;
		private final float defaultLightValue;
		
		public GetAmbientOcclusionLightValueEvent(IBlockState state,
			float lightValue)
		{
			this.state = state;
			this.lightValue = lightValue;
			defaultLightValue = lightValue;
		}
		
		public IBlockState getState()
		{
			return state;
		}
		
		public float getLightValue()
		{
			return lightValue;
		}
		
		public void setLightValue(float lightValue)
		{
			this.lightValue = lightValue;
		}
		
		public float getDefaultLightValue()
		{
			return defaultLightValue;
		}
		
		@Override
		public void fire(
			ArrayList<GetAmbientOcclusionLightValueListener> listeners)
		{
			for(GetAmbientOcclusionLightValueListener listener : listeners)
				listener.onGetAmbientOcclusionLightValue(this);
		}
		
		@Override
		public Class<GetAmbientOcclusionLightValueListener> getListenerType()
		{
			return GetAmbientOcclusionLightValueListener.class;
		}
	}
}
