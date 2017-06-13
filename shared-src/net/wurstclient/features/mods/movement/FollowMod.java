/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.movement;

import net.minecraft.entity.Entity;
import net.wurstclient.ai.FollowAI;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.listeners.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Mod;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;
import net.wurstclient.utils.ChatUtils;
import net.wurstclient.utils.EntityUtils;
import net.wurstclient.utils.EntityUtils.TargetSettings;

@Mod.Bypasses(ghostMode = false)
@Mod.DontSaveState
public final class FollowMod extends Mod implements UpdateListener
{
	private final float range = 12F;
	
	private Entity entity;
	private FollowAI ai;
	
	private final SliderSetting distance =
		new SliderSetting("Distance", 1F, 1F, 12F, 0.5F, ValueDisplay.DECIMAL)
		{
			@Override
			public void update()
			{
				entity = null;
			}
		};
	
	public FollowMod()
	{
		super("Follow",
			"A bot that follows the closest entity.\n" + "Very annoying.");
		setCategory(Category.MOVEMENT);
	}
	
	@Override
	public void initSettings()
	{
		addSetting(distance);
	}
	
	private final TargetSettings targetSettingsFind = new TargetSettings()
	{
		@Override
		public boolean targetFriends()
		{
			return true;
		}
		
		@Override
		public float getRange()
		{
			return range;
		}
	};
	
	private final TargetSettings targetSettingsKeep = new TargetSettings()
	{
		@Override
		public boolean targetFriends()
		{
			return true;
		}
		
		@Override
		public boolean targetBehindWalls()
		{
			return true;
		}
		
		@Override
		public boolean targetPlayers()
		{
			return true;
		}
		
		@Override
		public boolean targetAnimals()
		{
			return true;
		}
		
		@Override
		public boolean targetMonsters()
		{
			return true;
		}
		
		@Override
		public boolean targetGolems()
		{
			return true;
		}
		
		@Override
		public boolean targetSleepingPlayers()
		{
			return true;
		}
		
		@Override
		public boolean targetInvisiblePlayers()
		{
			return true;
		}
		
		@Override
		public boolean targetInvisibleMobs()
		{
			return true;
		}
		
		@Override
		public boolean targetTeams()
		{
			return false;
		}
	};
	
	@Override
	public String getRenderName()
	{
		if(entity != null)
			return "Following " + entity.getName();
		else
			return "Follow";
	}
	
	@Override
	public void onEnable()
	{
		if(entity == null)
			entity = EntityUtils.getClosestEntity(targetSettingsFind);
		
		if(entity == null)
		{
			ChatUtils.error("Could not find a valid entity within 12 blocks.");
			setEnabled(false);
			return;
		}
		
		ai = new FollowAI(entity, distance.getValueF());
		wurst.events.add(UpdateListener.class, this);
		ChatUtils.message("Now following " + entity.getName());
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
		
		if(ai != null)
			ai.stop();
		
		if(entity != null)
			ChatUtils.message("No longer following " + entity.getName());
		
		entity = null;
	}
	
	@Override
	public void onUpdate()
	{
		// check if player died
		if(WMinecraft.getPlayer().getHealth() <= 0)
		{
			if(entity == null)
				ChatUtils.message("No longer following entity");
			setEnabled(false);
			return;
		}
		
		// check if entity died or entity disappeared
		if(!EntityUtils.isCorrectEntity(entity, targetSettingsKeep))
		{
			entity = EntityUtils.getClosestEntity(targetSettingsFind);
			
			if(entity == null)
			{
				ChatUtils.message("No longer following entity");
				setEnabled(false);
				return;
			}
			
			ai = new FollowAI(entity, distance.getValueF());
		}
		
		// go to entity
		ai.update();
	}
	
	public void setEntity(Entity entity)
	{
		this.entity = entity;
	}
}
