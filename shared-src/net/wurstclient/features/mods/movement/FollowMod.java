/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.movement;

import java.util.ArrayList;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.ai.PathFinder;
import net.wurstclient.ai.PathPos;
import net.wurstclient.ai.PathProcessor;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.RenderListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Feature;
import net.wurstclient.features.Mod;
import net.wurstclient.features.commands.PathCmd;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;
import net.wurstclient.utils.ChatUtils;
import net.wurstclient.utils.EntityUtils;
import net.wurstclient.utils.EntityUtils.TargetSettings;
import net.wurstclient.utils.RotationUtils;

@Mod.Bypasses
@Mod.DontSaveState
public final class FollowMod extends Mod
	implements UpdateListener, RenderListener
{
	private Entity entity;
	private EntityPathFinder pathFinder;
	private PathProcessor processor;
	private int ticksProcessing;
	private float distanceSq;
	
	private final SliderSetting distance =
		new SliderSetting("Distance", 1F, 1F, 12F, 0.5F, ValueDisplay.DECIMAL)
		{
			@Override
			public void update()
			{
				distanceSq = (float)Math.pow(getValue(), 2);
			}
		};
	private final CheckboxSetting useAi =
		new CheckboxSetting("Use AI (experimental)", false);
	
	public FollowMod()
	{
		super("Follow",
			"A bot that follows the closest entity.\n" + "Very annoying.");
		setCategory(Category.MOVEMENT);
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.commands.followCmd};
	}
	
	@Override
	public void initSettings()
	{
		addSetting(distance);
		addSetting(useAi);
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
		
		pathFinder = new EntityPathFinder();
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
		ticksProcessing = 0;
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
		
		// check if entity died or disappeared
		if(!EntityUtils.isCorrectEntity(entity, targetSettingsKeep))
		{
			entity = EntityUtils.getClosestEntityWithName(entity.getName(),
				targetSettingsKeep);
			
			if(entity == null)
			{
				ChatUtils.message("No longer following entity");
				setEnabled(false);
				return;
			}
			
			pathFinder = new EntityPathFinder();
			processor = null;
			ticksProcessing = 0;
		}
		
		if(useAi.isChecked())
		{
			// reset pathfinder
			if((processor == null || processor.isDone() || ticksProcessing >= 10
				|| !pathFinder.isPathStillValid(processor.getIndex()))
				&& (pathFinder.isDone() || pathFinder.isFailed()))
			{
				pathFinder = new EntityPathFinder();
				processor = null;
				ticksProcessing = 0;
			}
			
			// find path
			if(!pathFinder.isDone() && !pathFinder.isFailed())
			{
				PathProcessor.lockControls();
				RotationUtils.faceEntityClient(entity);
				pathFinder.think();
				pathFinder.formatPath();
				processor = pathFinder.getProcessor();
			}
			
			// process path
			if(!processor.isDone())
			{
				processor.process();
				ticksProcessing++;
			}
		}else
		{
			// jump if necessary
			if(WMinecraft.getPlayer().isCollidedHorizontally
				&& WMinecraft.getPlayer().onGround)
				WMinecraft.getPlayer().jump();
			
			// swim up if necessary
			if(WMinecraft.getPlayer().isInWater()
				&& WMinecraft.getPlayer().posY < entity.posY)
				WMinecraft.getPlayer().motionY += 0.04;
			
			// control height if flying
			if(!WMinecraft.getPlayer().onGround
				&& (WMinecraft.getPlayer().capabilities.isFlying
					|| wurst.mods.flightMod.isActive())
				&& WMinecraft.getPlayer().getDistanceSq(entity.posX,
					WMinecraft.getPlayer().posY, entity.posZ) <= WMinecraft
						.getPlayer().getDistanceSq(WMinecraft.getPlayer().posX,
							entity.posY, WMinecraft.getPlayer().posZ))
			{
				if(WMinecraft.getPlayer().posY > entity.posY + 1D)
					mc.gameSettings.keyBindSneak.pressed = true;
				else if(WMinecraft.getPlayer().posY < entity.posY - 1D)
					mc.gameSettings.keyBindJump.pressed = true;
			}else
			{
				mc.gameSettings.keyBindSneak.pressed = false;
				mc.gameSettings.keyBindJump.pressed = false;
			}
			
			// follow entity
			RotationUtils.faceEntityClient(entity);
			mc.gameSettings.keyBindForward.pressed =
				WMinecraft.getPlayer().getDistanceSq(entity.posX,
					WMinecraft.getPlayer().posY, entity.posZ) > distanceSq;
		}
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
		public EntityPathFinder()
		{
			super(new BlockPos(entity));
			setThinkTime(1);
		}
		
		@Override
		protected boolean checkDone()
		{
			return done = entity.getDistanceSqToCenter(current) <= distanceSq;
		}
		
		@Override
		public ArrayList<PathPos> formatPath()
		{
			if(!done)
				failed = true;
			
			return super.formatPath();
		}
	}
}
