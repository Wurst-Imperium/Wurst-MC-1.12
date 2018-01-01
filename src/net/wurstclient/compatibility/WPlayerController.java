/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.compatibility;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class WPlayerController
{
	private static PlayerControllerMP getPlayerController()
	{
		return Minecraft.getMinecraft().playerController;
	}
	
	public static ItemStack windowClick_PICKUP(int slot)
	{
		return getPlayerController().windowClick(0, slot, 0, ClickType.PICKUP,
			WMinecraft.getPlayer());
	}
	
	public static ItemStack windowClick_QUICK_MOVE(int slot)
	{
		return getPlayerController().windowClick(0, slot, 0,
			ClickType.QUICK_MOVE, WMinecraft.getPlayer());
	}
	
	public static ItemStack windowClick_THROW(int slot)
	{
		return getPlayerController().windowClick(0, slot, 1, ClickType.THROW,
			WMinecraft.getPlayer());
	}
	
	public static void processRightClick()
	{
		getPlayerController().processRightClick(WMinecraft.getPlayer(),
			WMinecraft.getWorld(), EnumHand.MAIN_HAND);
	}
	
	public static void processRightClickBlock(BlockPos pos, EnumFacing side,
		Vec3d hitVec)
	{
		getPlayerController().processRightClickBlock(WMinecraft.getPlayer(),
			WMinecraft.getWorld(), pos, side, hitVec, EnumHand.MAIN_HAND);
	}
}
