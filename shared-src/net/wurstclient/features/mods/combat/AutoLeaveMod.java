/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.combat;

import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketPlayer;
import net.wurstclient.compatibility.WConnection;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.compatibility.WPlayer;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Feature;
import net.wurstclient.features.HelpPage;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;
import net.wurstclient.settings.ModeSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;

@SearchTags({"AutoDisconnect", "auto leave", "auto disconnect"})
@HelpPage("Mods/AutoLeave")
@Mod.Bypasses
public final class AutoLeaveMod extends Mod implements UpdateListener
{
	private final SliderSetting health =
		new SliderSetting("Health", 4, 0.5, 9.5, 0.5, ValueDisplay.DECIMAL);
	public final ModeSetting mode = new ModeSetting("Mode",
		new String[]{"Quit", "Chars", "TP", "SelfHurt"}, 0);
	
	public AutoLeaveMod()
	{
		super("AutoLeave",
			"Automatically leaves the server when your health is low.\n"
				+ "The Chars, TP and SelfHurt modes can bypass CombatLog and similar plugins.");
		setCategory(Category.COMBAT);
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.commands.leaveCmd};
	}
	
	@Override
	public String getRenderName()
	{
		return getName() + " [" + mode.getSelectedMode() + "]";
	}
	
	@Override
	public void initSettings()
	{
		addSetting(health);
		addSetting(mode);
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
		// check gamemode
		if(WMinecraft.getPlayer().capabilities.isCreativeMode)
			return;
		
		// check for other players
		if(mc.isSingleplayer()
			|| WMinecraft.getConnection().getPlayerInfoMap().size() == 1)
			return;
		
		// check health
		if(WMinecraft.getPlayer().getHealth() > health.getValueF() * 2F)
			return;
		
		// leave server
		switch(mode.getSelected())
		{
			case 0:
			WMinecraft.getWorld().sendQuittingDisconnectingPacket();
			break;
			
			case 1:
			WConnection.sendPacket(new CPacketChatMessage("§"));
			break;
			
			case 2:
			WConnection.sendPacket(
				new CPacketPlayer.Position(3.1e7, 100, 3.1e7, false));
			break;
			
			case 3:
			WPlayer.sendAttackPacket(WMinecraft.getPlayer());
			break;
		}
		
		// disable
		setEnabled(false);
	}
}
