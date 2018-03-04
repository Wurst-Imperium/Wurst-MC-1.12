/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.combat;

import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.compatibility.WPlayer;
import net.wurstclient.events.LeftClickListener;
import net.wurstclient.events.UpdateListener;
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

@SearchTags({"ClickAimbot", "click aura", "click aimbot"})
@Mod.Bypasses(ghostMode = false)
public final class ClickAuraMod extends Mod
	implements UpdateListener, LeftClickListener
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
					fov.lock(killaura.fov);
					hitThroughWalls.lock(killaura.hitThroughWalls);
				}else
				{
					if(useCooldown != null)
						useCooldown.unlock();
					
					speed.unlock();
					range.unlock();
					fov.unlock();
					hitThroughWalls.unlock();
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
	private final SliderSetting fov =
		new SliderSetting("FOV", 360, 30, 360, 10, ValueDisplay.DEGREES);
	private final CheckboxSetting hitThroughWalls =
		new CheckboxSetting("Hit through walls", false);
	
	public ClickAuraMod()
	{
		super("ClickAura",
			"Automatically attacks the closest valid entity whenever you click.\n\n"
				+ "§c§lWARNING:§r ClickAuras generally look more suspicious than Killauras and are easier to\n"
				+ "detect. It is recommended to use Killaura or TriggerBot instead.");
		setCategory(Category.COMBAT);
	}
	
	private final TargetSettings targetSettings = new TargetSettings()
	{
		@Override
		public boolean targetBehindWalls()
		{
			return hitThroughWalls.isChecked();
		}
		
		@Override
		public float getRange()
		{
			return range.getValueF();
		}
		
		@Override
		public float getFOV()
		{
			return fov.getValueF();
		}
	};
	
	@Override
	public void initSettings()
	{
		addSetting(useKillaura);
		
		if(useCooldown != null)
			addSetting(useCooldown);
		
		addSetting(speed);
		addSetting(range);
		addSetting(fov);
		addSetting(hitThroughWalls);
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.mods.killauraMod, wurst.mods.triggerBotMod,
			wurst.special.targetSpf};
	}
	
	@Override
	public void onEnable()
	{
		// disable other killauras
		wurst.mods.fightBotMod.setEnabled(false);
		wurst.mods.killauraLegitMod.setEnabled(false);
		wurst.mods.killauraMod.setEnabled(false);
		wurst.mods.multiAuraMod.setEnabled(false);
		wurst.mods.protectMod.setEnabled(false);
		wurst.mods.tpAuraMod.setEnabled(false);
		wurst.mods.triggerBotMod.setEnabled(false);
		
		wurst.events.add(UpdateListener.class, this);
		wurst.events.add(LeftClickListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
		wurst.events.remove(LeftClickListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		// update timer
		updateMS();
		
		// check if clicking
		if(!GameSettings.isKeyDown(mc.gameSettings.keyBindAttack))
			return;
		
		// check timer / cooldown
		if(useCooldown != null && useCooldown.isChecked()
			? WPlayer.getCooldown() < 1 : !hasTimePassedS(speed.getValueF()))
			return;
		
		attack();
	}
	
	@Override
	public void onLeftClick(LeftClickEvent event)
	{
		attack();
	}
	
	private void attack()
	{
		// set entity
		Entity entity = EntityUtils.getBestEntityToAttack(targetSettings);
		if(entity == null)
			return;
		
		// attack entity
		WPlayer.prepareAttack();
		RotationUtils.faceVectorPacketInstant(entity.boundingBox.getCenter());
		WPlayer.attackEntity(entity);
		
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
			break;
			
			case ANTICHEAT:
			case OLDER_NCP:
			case LATEST_NCP:
			speed.setUsableMax(12);
			range.setUsableMax(4.25);
			break;
			
			case GHOST_MODE:
			speed.setUsableMax(12);
			range.setUsableMax(4.25);
			break;
		}
	}
}
