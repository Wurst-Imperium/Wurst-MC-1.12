/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.other;

import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;

@SearchTags({"legit", "disable"})
@Mod.Bypasses
public final class PanicMod extends Mod implements UpdateListener
{
	public PanicMod()
	{
		super("Panic", "Instantly turns off all enabled mods.\n"
			+ "Be careful with this!");
		setCategory(Category.OTHER);
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
		for(Mod mod : wurst.mods.getAllMods())
			if(mod.isEnabled() && mod != this)
				mod.setEnabled(false);
		setEnabled(false);
	}
}
