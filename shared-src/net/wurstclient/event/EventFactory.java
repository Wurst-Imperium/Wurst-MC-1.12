/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.event;

import net.minecraft.client.entity.EntityPlayerSP;
import net.wurstclient.WurstClient;
import net.wurstclient.events.CameraTransformViewBobbingListener.CameraTransformViewBobbingEvent;
import net.wurstclient.events.PlayerMoveListener.PlayerMoveEvent;

public final class EventFactory
{
	public static boolean cameraTransformViewBobbing()
	{
		CameraTransformViewBobbingEvent event =
			new CameraTransformViewBobbingEvent();
		WurstClient.INSTANCE.events.fire(event);
		return !event.isCancelled();
	}
	
	public static void onPlayerMove(EntityPlayerSP player)
	{
		WurstClient.INSTANCE.events.fire(new PlayerMoveEvent(player));
	}
}
