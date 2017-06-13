/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.compatibility;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;

public final class WPotion
{
	public static List<PotionEffect> getEffectsFromStack(ItemStack stack)
	{
		return PotionUtils.getEffectsFromStack(stack);
	}
	
	public static int getIdFromEffect(PotionEffect effect)
	{
		return Potion.getIdFromPotion(effect.getPotion());
	}
	
	public static int getIdFromResourceLocation(String location)
	{
		return Potion
			.getIdFromPotion(Potion.getPotionFromResourceLocation(location));
	}
}
