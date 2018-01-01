/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.utils;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.compatibility.WPlayer;

public class EntityFakePlayer extends EntityOtherPlayerMP
{
	public EntityFakePlayer()
	{
		super(WMinecraft.getWorld(), WMinecraft.getPlayer().getGameProfile());
		copyLocationAndAnglesFrom(WMinecraft.getPlayer());
		
		// fix inventory
		inventory.copyInventory(WMinecraft.getPlayer().inventory);
		WPlayer.copyPlayerModel(WMinecraft.getPlayer(), this);
		
		// fix rotation
		rotationYawHead = WMinecraft.getPlayer().rotationYawHead;
		renderYawOffset = WMinecraft.getPlayer().renderYawOffset;
		
		// fix cape movement
		chasingPosX = posX;
		chasingPosY = posY;
		chasingPosZ = posZ;
		
		// spawn
		WMinecraft.getWorld().addEntityToWorld(getEntityId(), this);
	}
	
	public void resetPlayerPosition()
	{
		WMinecraft.getPlayer().setPositionAndRotation(posX, posY, posZ,
			rotationYaw, rotationPitch);
	}
	
	public void despawn()
	{
		WMinecraft.getWorld().removeEntityFromWorld(getEntityId());
	}
}
