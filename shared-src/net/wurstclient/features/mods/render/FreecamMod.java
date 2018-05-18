/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.render;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.PlayerMoveListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Feature;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;
import net.wurstclient.utils.EntityFakePlayer;

@SearchTags({"free camera", "spectator"})
@Mod.Bypasses
@Mod.DontSaveState
public final class FreecamMod extends Mod
	implements UpdateListener, PlayerMoveListener
{
	private final SliderSetting speed =
		new SliderSetting("Speed", 1, 0.05, 10, 0.05, ValueDisplay.DECIMAL);
	
	private EntityFakePlayer fakePlayer;
	
	public FreecamMod()
	{
		super("Freecam",
			"Allows you to move the camera without moving your character.");
		setCategory(Category.RENDER);
		addSetting(speed);
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.mods.remoteViewMod};
	}
	
	@Override
	public void onEnable()
	{
		wurst.events.add(UpdateListener.class, this);
		wurst.events.add(PlayerMoveListener.class, this);
		
		fakePlayer = new EntityFakePlayer();
		
		GameSettings gs = mc.gameSettings;
		KeyBinding[] bindings = {gs.keyBindForward, gs.keyBindBack,
			gs.keyBindLeft, gs.keyBindRight, gs.keyBindJump, gs.keyBindSneak};
		
		for(KeyBinding binding : bindings)
			binding.pressed = GameSettings.isKeyDown(binding);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
		wurst.events.remove(PlayerMoveListener.class, this);
		
		fakePlayer.resetPlayerPosition();
		fakePlayer.despawn();
		
		EntityPlayerSP player = WMinecraft.getPlayer();
		player.motionX = 0;
		player.motionY = 0;
		player.motionZ = 0;
		
		mc.renderGlobal.loadRenderers();
	}
	
	@Override
	public void onUpdate()
	{
		EntityPlayerSP player = WMinecraft.getPlayer();
		player.motionX = 0;
		player.motionY = 0;
		player.motionZ = 0;
		
		player.onGround = false;
		player.jumpMovementFactor = speed.getValueF();
		
		if(mc.gameSettings.keyBindJump.pressed)
			player.motionY += speed.getValue();
		
		if(mc.gameSettings.keyBindSneak.pressed)
			player.motionY -= speed.getValue();
	}
	
	@Override
	public void onPlayerMove(EntityPlayerSP player)
	{
		player.noClip = true;
	}
}
