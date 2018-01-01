/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.combat;

import net.wurstclient.features.Category;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;

@SearchTags({"AntiVelocity", "NoKnockback", "AntiKB", "anti knockback",
	"anti velocity", "no knockback", "anti kb"})
@Mod.Bypasses(ghostMode = false)
public final class AntiKnockbackMod extends Mod
{
	private final SliderSetting strength = new SliderSetting("Strength", 1,
		0.01, 1, 0.01, ValueDisplay.PERCENTAGE);
	
	public AntiKnockbackMod()
	{
		super("AntiKnockback",
			"Prevents you from getting pushed by players, mobs and flowing water.");
		setCategory(Category.COMBAT);
	}
	
	@Override
	public void initSettings()
	{
		addSetting(strength);
	}
	
	public float getKnockbackModifier()
	{
		return isActive() ? 1 - strength.getValueF() : 1;
	}
}
