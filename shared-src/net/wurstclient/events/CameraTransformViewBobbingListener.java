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

public interface CameraTransformViewBobbingListener extends Listener
{
	public void onCameraTransformViewBobbing(
		CameraTransformViewBobbingEvent event);
	
	public static class CameraTransformViewBobbingEvent
		extends CancellableEvent<CameraTransformViewBobbingListener>
	{
		@Override
		public void fire(
			ArrayList<CameraTransformViewBobbingListener> listeners)
		{
			for(CameraTransformViewBobbingListener listener : listeners)
			{
				listener.onCameraTransformViewBobbing(this);
				
				if(isCancelled())
					break;
			}
		}
		
		@Override
		public Class<CameraTransformViewBobbingListener> getListenerType()
		{
			return CameraTransformViewBobbingListener.class;
		}
	}
}
