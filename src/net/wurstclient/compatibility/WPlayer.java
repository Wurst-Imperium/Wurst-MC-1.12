/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.compatibility;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;

public final class WPlayer
{
	public static void swingArmClient()
	{
		WMinecraft.getPlayer().swingArm(EnumHand.MAIN_HAND);
	}
	
	public static void swingArmPacket()
	{
		WConnection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
	}
	
	public static float getCooldown()
	{
		return WMinecraft.getPlayer().getCooledAttackStrength(0);
	}
	
	public static void addPotionEffect(Potion potion)
	{
		WMinecraft.getPlayer()
			.addPotionEffect(new PotionEffect(potion, 10801220));
	}
	
	public static void removePotionEffect(Potion potion)
	{
		WMinecraft.getPlayer().removePotionEffect(potion);
	}
	
	public static void copyPlayerModel(EntityPlayer from, EntityPlayer to)
	{
		to.getDataManager().set(EntityPlayer.PLAYER_MODEL_FLAG,
			from.getDataManager().get(EntityPlayer.PLAYER_MODEL_FLAG));
	}
}
