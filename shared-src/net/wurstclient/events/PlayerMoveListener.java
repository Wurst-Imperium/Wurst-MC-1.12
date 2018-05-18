/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.events;

import java.util.ArrayList;

import net.minecraft.client.entity.EntityPlayerSP;
import net.wurstclient.event.Event;
import net.wurstclient.event.Listener;

public interface PlayerMoveListener extends Listener
{
	public void onPlayerMove(EntityPlayerSP player);
	
	public static class PlayerMoveEvent extends Event<PlayerMoveListener>
	{
		private final EntityPlayerSP player;
		
		public PlayerMoveEvent(EntityPlayerSP player)
		{
			this.player = player;
		}
		
		@Override
		public void fire(ArrayList<PlayerMoveListener> listeners)
		{
			for(PlayerMoveListener listener : listeners)
				listener.onPlayerMove(player);
		}
		
		@Override
		public Class<PlayerMoveListener> getListenerType()
		{
			return PlayerMoveListener.class;
		}
	}
}
