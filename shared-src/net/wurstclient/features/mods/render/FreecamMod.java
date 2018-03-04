/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.render;

import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Feature;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;
import net.wurstclient.utils.EntityFakePlayer;

@SearchTags({"free camera", "spectator"})
@Mod.Bypasses
@Mod.DontSaveState
public final class FreecamMod extends Mod implements UpdateListener
{
	private EntityFakePlayer fakePlayer;
	
	public FreecamMod()
	{
		super("Freecam", "Allows you to fly out of your body.");
		setCategory(Category.RENDER);
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.mods.remoteViewMod, wurst.mods.flightMod};
	}
	
	@Override
	public void onEnable()
	{
		fakePlayer = new EntityFakePlayer();
		
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
		
		fakePlayer.resetPlayerPosition();
		fakePlayer.despawn();
		
		mc.renderGlobal.loadRenderers();
	}
	
	@Override
	public void onUpdate()
	{
		WMinecraft.getPlayer().motionX = 0;
		WMinecraft.getPlayer().motionY = 0;
		WMinecraft.getPlayer().motionZ = 0;
		
		WMinecraft.getPlayer().jumpMovementFactor =
			wurst.mods.flightMod.speed.getValueF() / 10F;
		
		if(mc.gameSettings.keyBindJump.pressed)
			WMinecraft.getPlayer().motionY +=
				wurst.mods.flightMod.speed.getValue();
		
		if(mc.gameSettings.keyBindSneak.pressed)
			WMinecraft.getPlayer().motionY -=
				wurst.mods.flightMod.speed.getValue();
	}
}
