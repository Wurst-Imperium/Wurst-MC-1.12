/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.movement;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.ai.PathFinder;
import net.wurstclient.ai.PathProcessor;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.listeners.RenderListener;
import net.wurstclient.events.listeners.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Mod;
import net.wurstclient.features.commands.PathCmd;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;
import net.wurstclient.utils.ChatUtils;
import net.wurstclient.utils.EntityUtils;
import net.wurstclient.utils.EntityUtils.TargetSettings;

@Mod.Bypasses
@Mod.DontSaveState
public final class FollowMod extends Mod
	implements UpdateListener, RenderListener
{
	private Entity entity;
	private EntityPathFinder pathFinder;
	private PathProcessor processor;
	
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
		{
			entity = EntityUtils.getClosestEntity(targetSettingsFind);
			
			if(entity == null)
			{
				ChatUtils.error("Could not find a valid entity.");
				setEnabled(false);
				return;
			}
		}
		
		pathFinder = new EntityPathFinder(entity, distance.getValueF());
		pathFinder.setThinkTime(40);
		wurst.events.add(UpdateListener.class, this);
		wurst.events.add(RenderListener.class, this);
		ChatUtils.message("Now following " + entity.getName());
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
		wurst.events.remove(RenderListener.class, this);
		
		pathFinder = null;
		processor = null;
		PathProcessor.releaseControls();
		
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
			
			pathFinder = new EntityPathFinder(entity, distance.getValueF());
			pathFinder.setThinkTime(40);
		}
		
		// find path
		if(!pathFinder.isDone() && !pathFinder.isFailed())
		{
			PathProcessor.lockControls();
			
			pathFinder.think();
			
			if(!pathFinder.isDone() && !pathFinder.isFailed())
				return;
			
			pathFinder.formatPath();
			
			// set processor
			processor = pathFinder.getProcessor();
		}
		
		// check path
		if(processor != null
			&& !pathFinder.isPathStillValid(processor.getIndex()))
		{
			pathFinder = new EntityPathFinder(pathFinder);
			return;
		}
		
		// process path
		if(!processor.isDone())
			processor.process();
		else
			PathProcessor.lockControls();
	}
	
	@Override
	public void onRender(float partialTicks)
	{
		PathCmd pathCmd = wurst.commands.pathCmd;
		pathFinder.renderPath(pathCmd.isDebugMode(), pathCmd.isDepthTest());
	}
	
	public void setEntity(Entity entity)
	{
		this.entity = entity;
	}
	
	private class EntityPathFinder extends PathFinder
	{
		private final Entity entity;
		private final float distanceSq;
		
		public EntityPathFinder(Entity entity, float distance)
		{
			super(new BlockPos(entity));
			this.entity = entity;
			distanceSq = (float)Math.pow(distance, 2);
		}
		
		public EntityPathFinder(EntityPathFinder pathFinder)
		{
			super(new BlockPos(pathFinder.entity));
			thinkSpeed = pathFinder.thinkSpeed;
			thinkTime = pathFinder.thinkTime;
			entity = pathFinder.entity;
			distanceSq = pathFinder.distanceSq;
		}
		
		@Override
		protected boolean checkDone()
		{
			return done = entity.getDistanceSqToCenter(current) <= distanceSq;
		}
		
		@Override
		public boolean isPathStillValid(int index)
		{
			if(!super.isPathStillValid(index))
				return false;
			
			// check entity
			if(!getGoal().equals(new BlockPos(entity)))
				return false;
			
			return true;
		}
	}
}
