/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.retro;

import net.minecraft.network.play.client.CPacketPlayer;
import net.wurstclient.compatibility.WConnection;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Mod;
import net.wurstclient.features.RetroMod;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;

@Mod.Bypasses(ghostMode = false, latestNCP = false, olderNCP = false)
public final class RegenMod extends RetroMod implements UpdateListener
{
	private final SliderSetting speed =
		new SliderSetting("Speed", 100, 10, 1000, 10, ValueDisplay.INTEGER);
	private final CheckboxSetting pauseInMidAir =
		new CheckboxSetting("Pause in mid-air", true);
	
	public RegenMod()
	{
		super("Regen", "Regenerates your health much faster.\n"
			+ "Can sometimes get you kicked for \"Flying is not enabled\".");
		setCategory(Category.RETRO);
	}
	
	@Override
	public void initSettings()
	{
		addSetting(speed);
		addSetting(pauseInMidAir);
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
		if(WMinecraft.getPlayer().capabilities.isCreativeMode
			|| WMinecraft.getPlayer().getHealth() == 0)
			return;
		
		if(pauseInMidAir.isChecked() && !WMinecraft.getPlayer().onGround)
			return;
		
		if(WMinecraft.getPlayer().getFoodStats().getFoodLevel() < 18)
			return;
		
		if(WMinecraft.getPlayer().getHealth() >= WMinecraft.getPlayer()
			.getMaxHealth())
			return;
		
		for(int i = 0; i < speed.getValueI(); i++)
			WConnection.sendPacket(new CPacketPlayer());
	}
}
