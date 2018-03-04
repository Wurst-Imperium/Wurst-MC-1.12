/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.other;

import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Mod;

@Mod.Bypasses(ghostMode = false)
public final class ThrowMod extends Mod implements UpdateListener
{
	public ThrowMod()
	{
		super("Throw", "Uses an item multiple times.\n"
			+ "This can cause a lot of lag and even crash a server.\n"
			+ "Works best with snowballs or eggs.\n"
			+ "Use the .throw command to change the amount of uses per click.");
		setCategory(Category.OTHER);
	}
	
	@Override
	public String getRenderName()
	{
		return getName() + " [" + wurst.options.throwAmount + "]";
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
	}
	
	@Override
	public void onUpdate()
	{
		if((mc.rightClickDelayTimer == 4 || wurst.mods.fastPlaceMod.isActive())
			&& mc.gameSettings.keyBindUseItem.pressed)
		{
			if(mc.objectMouseOver == null
				|| WMinecraft.getPlayer().inventory.getCurrentItem() == null)
				return;
			for(int i = 0; i < wurst.options.throwAmount - 1; i++)
				mc.rightClickMouse();
		}
	}
}
