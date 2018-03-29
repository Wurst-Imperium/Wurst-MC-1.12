/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.movement;

import net.minecraft.client.settings.GameSettings;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.wurstclient.compatibility.WConnection;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.PostUpdateListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;
import net.wurstclient.features.special_features.YesCheatSpf.Profile;
import net.wurstclient.settings.ModeSetting;

@SearchTags({"AutoSneaking"})
@Mod.Bypasses
public final class SneakMod extends Mod
	implements UpdateListener, PostUpdateListener
{
	private final ModeSetting mode = new ModeSetting("Mode",
		"§lPacket§r mode makes it look like you're sneaking without slowing you down.\n"
			+ "§lLegit§r mode actually makes you sneak.",
		new String[]{"Packet", "Legit"}, 0);
	
	public SneakMod()
	{
		super("Sneak", "Makes you sneak automatically.");
		setCategory(Category.MOVEMENT);
	}
	
	@Override
	public void initSettings()
	{
		addSetting(mode);
	}
	
	@Override
	public String getRenderName()
	{
		return getName() + " [" + mode.getSelectedMode() + "]";
	}
	
	@Override
	public void onEnable()
	{
		wurst.events.add(UpdateListener.class, this);
		wurst.events.add(PostUpdateListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
		wurst.events.remove(PostUpdateListener.class, this);
		
		switch(mode.getSelected())
		{
			case 0:
			WConnection.sendPacket(new CPacketEntityAction(
				WMinecraft.getPlayer(), Action.STOP_SNEAKING));
			break;
			
			case 1:
			mc.gameSettings.keyBindSneak.pressed =
				GameSettings.isKeyDown(mc.gameSettings.keyBindSneak);
			break;
		}
	}
	
	@Override
	public void onUpdate()
	{
		switch(mode.getSelected())
		{
			case 0:
			WConnection.sendPacket(new CPacketEntityAction(
				WMinecraft.getPlayer(), Action.START_SNEAKING));
			WConnection.sendPacket(new CPacketEntityAction(
				WMinecraft.getPlayer(), Action.STOP_SNEAKING));
			break;
			
			case 1:
			mc.gameSettings.keyBindSneak.pressed = true;
			break;
		}
	}
	
	@Override
	public void afterUpdate()
	{
		if(mode.getSelected() == 1)
			return;
		
		WConnection.sendPacket(new CPacketEntityAction(WMinecraft.getPlayer(),
			Action.STOP_SNEAKING));
		WConnection.sendPacket(new CPacketEntityAction(WMinecraft.getPlayer(),
			Action.START_SNEAKING));
	}
	
	@Override
	public void onYesCheatUpdate(Profile profile)
	{
		switch(profile)
		{
			case GHOST_MODE:
			mode.lock(1);
			break;
			
			default:
			mode.unlock();
			break;
		}
	}
}
