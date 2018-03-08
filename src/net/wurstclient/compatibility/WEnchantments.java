/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.compatibility;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;

public final class WEnchantments
{
	public static final Enchantment EFFICIENCY = Enchantments.EFFICIENCY;
	public static final Enchantment SILK_TOUCH = Enchantments.SILK_TOUCH;
	
	public static int getEnchantmentLevel(Enchantment enchantment,
		ItemStack stack)
	{
		return EnchantmentHelper.getEnchantmentLevel(enchantment, stack);
	}
}
