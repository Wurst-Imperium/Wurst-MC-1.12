/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.fun;

import java.util.Random;
import java.util.Set;

import net.minecraft.entity.player.EnumPlayerModelParts;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;

@SearchTags({"SkinBlinker", "SpookySkin", "skin blinker", "spooky skin"})
@Mod.Bypasses(ghostMode = false)
public final class SkinDerpMod extends Mod implements UpdateListener
{
	private final Random random = new Random();
	
	public SkinDerpMod()
	{
		super("SkinDerp", "Randomly toggles parts of your skin.");
		setCategory(Category.FUN);
	}
	
	@Override
	public void onEnable()
	{
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
		
		for(EnumPlayerModelParts part : EnumPlayerModelParts.values())
			mc.gameSettings.setModelPartEnabled(part, true);
	}
	
	@Override
	public void onUpdate()
	{
		if(random.nextInt(4) != 0)
			return;
		
		Set activeParts = mc.gameSettings.getModelParts();
		for(EnumPlayerModelParts part : EnumPlayerModelParts.values())
			mc.gameSettings.setModelPartEnabled(part,
				!activeParts.contains(part));
	}
}
