/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.combat;

import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.DeathListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;

@SearchTags({"auto respawn"})
@Mod.Bypasses
public final class AutoRespawnMod extends Mod implements DeathListener
{
	public AutoRespawnMod()
	{
		super("AutoRespawn", "Automatically respawns you whenever you die.");
		setCategory(Category.COMBAT);
	}
	
	@Override
	public void onEnable()
	{
		wurst.events.add(DeathListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(DeathListener.class, this);
	}
	
	@Override
	public void onDeath()
	{
		WMinecraft.getPlayer().respawnPlayer();
		mc.displayGuiScreen(null);
	}
}
