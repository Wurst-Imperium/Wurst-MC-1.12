/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.combat;

import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.compatibility.WPlayer;
import net.wurstclient.events.listeners.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Feature;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;
import net.wurstclient.features.special_features.YesCheatSpf.Profile;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;
import net.wurstclient.utils.EntityUtils;
import net.wurstclient.utils.EntityUtils.TargetSettings;
import net.wurstclient.utils.RotationUtils;

@SearchTags({"fight bot"})
@Mod.Bypasses(ghostMode = false)
@Mod.DontSaveState
public final class FightBotMod extends Mod implements UpdateListener
{
	private final CheckboxSetting useKillaura =
		new CheckboxSetting("Use Killaura settings", true)
		{
			@Override
			public void update()
			{
				if(isChecked())
				{
					KillauraMod killaura = wurst.mods.killauraMod;
					
					if(useCooldown != null)
						useCooldown.lock(killaura.useCooldown);
					
					speed.lock(killaura.speed);
					range.lock(killaura.range);
				}else
				{
					if(useCooldown != null)
						useCooldown.unlock();
					
					speed.unlock();
					range.unlock();
				}
			}
		};
	private final CheckboxSetting useCooldown = !WMinecraft.COOLDOWN ? null
		: new CheckboxSetting("Use Attack Cooldown as Speed", true)
		{
			@Override
			public void update()
			{
				speed.setDisabled(isChecked());
			}
		};
	private final SliderSetting speed =
		new SliderSetting("Speed", 12, 0.1, 20, 0.1, ValueDisplay.DECIMAL);
	private final SliderSetting range =
		new SliderSetting("Range", 4.25, 1, 6, 0.05, ValueDisplay.DECIMAL);
	private final SliderSetting distance =
		new SliderSetting("Distance", 3, 1, 6, 0.05, ValueDisplay.DECIMAL);
	
	private final TargetSettings followSettings = new TargetSettings();
	private final TargetSettings attackSettings = new TargetSettings()
	{
		@Override
		public float getRange()
		{
			return range.getValueF();
		}
	};
	
	public FightBotMod()
	{
		super("FightBot",
			"A bot that automatically walks around and kills everything.\n"
				+ "Good for MobArena.");
		setCategory(Category.COMBAT);
	}
	
	@Override
	public void initSettings()
	{
		addSetting(useKillaura);
		
		if(useCooldown != null)
			addSetting(useCooldown);
		
		addSetting(speed);
		addSetting(range);
		addSetting(distance);
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.mods.followMod, wurst.mods.protectMod,
			wurst.special.targetSpf, wurst.special.yesCheatSpf};
	}
	
	@Override
	public void onEnable()
	{
		// disable other killauras
		wurst.mods.clickAuraMod.setEnabled(false);
		wurst.mods.killauraLegitMod.setEnabled(false);
		wurst.mods.killauraMod.setEnabled(false);
		wurst.mods.multiAuraMod.setEnabled(false);
		wurst.mods.protectMod.setEnabled(false);
		wurst.mods.tpAuraMod.setEnabled(false);
		wurst.mods.triggerBotMod.setEnabled(false);
		
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		// remove listener
		wurst.events.remove(UpdateListener.class, this);
		
		// reset keys
		resetKeys();
	}
	
	@Override
	public void onUpdate()
	{
		// update timer
		updateMS();
		
		// reset keys
		resetKeys();
		
		// set entity
		Entity entity = EntityUtils.getClosestEntity(followSettings);
		if(entity == null)
			return;
		
		// jump if necessary
		if(WMinecraft.getPlayer().isCollidedHorizontally)
			mc.gameSettings.keyBindJump.pressed = true;
		
		// swim up if necessary
		if(WMinecraft.getPlayer().isInWater()
			&& WMinecraft.getPlayer().posY < entity.posY)
			mc.gameSettings.keyBindJump.pressed = true;
		
		// control height if flying
		if(!WMinecraft.getPlayer().onGround
			&& (WMinecraft.getPlayer().capabilities.isFlying
				|| wurst.mods.flightMod.isActive())
			&& Math.sqrt(
				Math.pow(WMinecraft.getPlayer().posX - entity.posX, 2) + Math
					.pow(WMinecraft.getPlayer().posZ - entity.posZ, 2)) <= range
						.getValue())
			if(WMinecraft.getPlayer().posY > entity.posY + 1D)
				mc.gameSettings.keyBindSneak.pressed = true;
			else if(WMinecraft.getPlayer().posY < entity.posY - 1D)
				mc.gameSettings.keyBindJump.pressed = true;
			
		// follow entity
		mc.gameSettings.keyBindForward.pressed = WMinecraft.getPlayer()
			.getDistanceToEntity(entity) > distance.getValueF();
		if(!RotationUtils.faceEntityClient(entity))
			return;
		
		// check timer / cooldown
		if(useCooldown != null && useCooldown.isChecked()
			? WPlayer.getCooldown() < 1 : !hasTimePassedS(speed.getValueF()))
			return;
		
		// check range
		if(!EntityUtils.isCorrectEntity(entity, attackSettings))
			return;
		
		// attack entity
		EntityUtils.prepareAttack();
		EntityUtils.attackEntity(entity);
		
		// reset timer
		updateLastMS();
	}
	
	@Override
	public void onYesCheatUpdate(Profile profile)
	{
		switch(profile)
		{
			default:
			case OFF:
			case MINEPLEX:
			speed.resetUsableMax();
			range.resetUsableMax();
			distance.resetUsableMax();
			break;
			
			case ANTICHEAT:
			case OLDER_NCP:
			case LATEST_NCP:
			case GHOST_MODE:
			speed.setUsableMax(12);
			range.setUsableMax(4.25);
			distance.setUsableMax(4.25);
			break;
		}
	}
	
	private void resetKeys()
	{
		// get keys
		KeyBinding[] keys = new KeyBinding[]{mc.gameSettings.keyBindForward,
			mc.gameSettings.keyBindJump, mc.gameSettings.keyBindSneak};
		
		// reset keys
		for(KeyBinding key : keys)
			key.pressed = GameSettings.isKeyDown(key);
	}
}
