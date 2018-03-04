/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.combat;

import java.util.ArrayList;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.ai.PathFinder;
import net.wurstclient.ai.PathPos;
import net.wurstclient.ai.PathProcessor;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.compatibility.WPlayer;
import net.wurstclient.events.RenderListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Mod;
import net.wurstclient.features.commands.PathCmd;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.utils.EntityUtils;
import net.wurstclient.utils.EntityUtils.TargetSettings;
import net.wurstclient.utils.RotationUtils;

@Mod.Bypasses(ghostMode = false)
@Mod.DontSaveState
public final class ProtectMod extends Mod
	implements UpdateListener, RenderListener
{
	private final CheckboxSetting useAi =
		new CheckboxSetting("Use AI (experimental)", false);
	
	private EntityPathFinder pathFinder;
	private PathProcessor processor;
	private int ticksProcessing;
	
	private Entity friend;
	private Entity enemy;
	
	private float range = 6F;
	private double distanceF = 2D;
	private double distanceE = 3D;
	
	private final TargetSettings friendSettingsFind = new TargetSettings()
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
	
	private final TargetSettings friendSettingsKeep = new TargetSettings()
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
	
	private final TargetSettings enemySettings = new TargetSettings()
	{
		@Override
		public float getRange()
		{
			return range;
		}
	};
	
	public ProtectMod()
	{
		super("Protect",
			"A bot that follows the closest entity and protects it from other entities.\n"
				+ "Use .protect <entity> to protect a specific entity instead of the closest one.");
		setCategory(Category.COMBAT);
	}
	
	@Override
	public void initSettings()
	{
		addSetting(useAi);
	}
	
	@Override
	public String getRenderName()
	{
		if(friend != null)
			return "Protecting " + friend.getName();
		else
			return "Protect";
	}
	
	@Override
	public void onEnable()
	{
		// disable other killauras
		wurst.mods.clickAuraMod.setEnabled(false);
		wurst.mods.fightBotMod.setEnabled(false);
		wurst.mods.killauraLegitMod.setEnabled(false);
		wurst.mods.killauraMod.setEnabled(false);
		wurst.mods.multiAuraMod.setEnabled(false);
		wurst.mods.tpAuraMod.setEnabled(false);
		wurst.mods.triggerBotMod.setEnabled(false);
		
		// set friend
		friend = EntityUtils.getClosestEntity(friendSettingsFind);
		pathFinder = new EntityPathFinder(friend, distanceF);
		
		wurst.events.add(UpdateListener.class, this);
		wurst.events.add(RenderListener.class, this);
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
		
		if(friend != null)
			mc.gameSettings.keyBindForward.pressed = false;
	}
	
	@Override
	public void onUpdate()
	{
		// update timer
		updateMS();
		
		// check if player died, friend died or friend disappeared
		if(WMinecraft.getPlayer().getHealth() <= 0
			|| !EntityUtils.isCorrectEntity(friend, friendSettingsKeep))
		{
			friend = null;
			enemy = null;
			setEnabled(false);
			return;
		}
		
		// set enemy
		enemy = EntityUtils.getClosestEntityOtherThan(friend, enemySettings);
		Entity target = enemy == null
			|| WMinecraft.getPlayer().getDistanceSqToEntity(friend) >= 576
				? friend : enemy;
		double distance = target == enemy ? distanceE : distanceF;
		
		if(useAi.isChecked())
		{
			// reset pathfinder
			if((processor == null || processor.isDone() || ticksProcessing >= 10
				|| !pathFinder.isPathStillValid(processor.getIndex()))
				&& (pathFinder.isDone() || pathFinder.isFailed()))
			{
				pathFinder = new EntityPathFinder(target, distance);
				processor = null;
				ticksProcessing = 0;
			}
			
			// find path
			if(!pathFinder.isDone() && !pathFinder.isFailed())
			{
				PathProcessor.lockControls();
				RotationUtils.faceEntityClient(target);
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
				&& WMinecraft.getPlayer().posY < target.posY)
				WMinecraft.getPlayer().motionY += 0.04;
			
			// control height if flying
			if(!WMinecraft.getPlayer().onGround
				&& (WMinecraft.getPlayer().capabilities.isFlying
					|| wurst.mods.flightMod.isActive())
				&& WMinecraft.getPlayer().getDistanceSq(target.posX,
					WMinecraft.getPlayer().posY, target.posZ) <= WMinecraft
						.getPlayer().getDistanceSq(WMinecraft.getPlayer().posX,
							target.posY, WMinecraft.getPlayer().posZ))
			{
				if(WMinecraft.getPlayer().posY > target.posY + 1D)
					mc.gameSettings.keyBindSneak.pressed = true;
				else if(WMinecraft.getPlayer().posY < target.posY - 1D)
					mc.gameSettings.keyBindJump.pressed = true;
			}else
			{
				mc.gameSettings.keyBindSneak.pressed = false;
				mc.gameSettings.keyBindJump.pressed = false;
			}
			
			// follow target
			RotationUtils.faceEntityClient(target);
			mc.gameSettings.keyBindForward.pressed =
				WMinecraft.getPlayer().getDistanceToEntity(
					target) > (target == friend ? distanceF : distanceE);
		}
		
		if(target == enemy)
		{
			// check timer / cooldown
			if(wurst.mods.killauraMod.useCooldown != null
				&& wurst.mods.killauraMod.useCooldown.isChecked()
					? WPlayer.getCooldown() < 1
					: !hasTimePassedS(wurst.mods.killauraMod.speed.getValueF()))
				return;
			
			// attack enemy
			WPlayer.prepareAttack();
			WPlayer.attackEntity(enemy);
			
			// reset timer
			updateLastMS();
		}
	}
	
	@Override
	public void onRender(float partialTicks)
	{
		PathCmd pathCmd = wurst.commands.pathCmd;
		pathFinder.renderPath(pathCmd.isDebugMode(), pathCmd.isDepthTest());
	}
	
	public void setFriend(Entity friend)
	{
		this.friend = friend;
	}
	
	private class EntityPathFinder extends PathFinder
	{
		private final Entity entity;
		private double distanceSq;
		
		public EntityPathFinder(Entity entity, double distance)
		{
			super(new BlockPos(entity));
			this.entity = entity;
			distanceSq = distance * distance;
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
