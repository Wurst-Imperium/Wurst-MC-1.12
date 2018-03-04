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
import net.wurstclient.features.Feature;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;
import net.wurstclient.features.commands.PathCmd;
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
public final class FightBotMod extends Mod
	implements UpdateListener, RenderListener
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
		new SliderSetting("Distance", 3, 1, 6, 0.05, ValueDisplay.DECIMAL)
		{
			@Override
			public void update()
			{
				distanceSq = Math.pow(getValue(), 2);
			}
		};
	
	private final CheckboxSetting useAi =
		new CheckboxSetting("Use AI (experimental)", false);
	
	private EntityPathFinder pathFinder;
	private PathProcessor processor;
	private int ticksProcessing;
	private double distanceSq;
	
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
		
		addSetting(useAi);
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
		
		pathFinder = new EntityPathFinder(WMinecraft.getPlayer());
		
		wurst.events.add(UpdateListener.class, this);
		wurst.events.add(RenderListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		// remove listener
		wurst.events.remove(UpdateListener.class, this);
		wurst.events.remove(RenderListener.class, this);
		
		pathFinder = null;
		processor = null;
		ticksProcessing = 0;
		PathProcessor.releaseControls();
	}
	
	@Override
	public void onUpdate()
	{
		// update timer
		updateMS();
		
		// set entity
		Entity entity = EntityUtils.getClosestEntity(followSettings);
		if(entity == null)
			return;
		
		if(useAi.isChecked())
		{
			// reset pathfinder
			if((processor == null || processor.isDone() || ticksProcessing >= 10
				|| !pathFinder.isPathStillValid(processor.getIndex()))
				&& (pathFinder.isDone() || pathFinder.isFailed()))
			{
				pathFinder = new EntityPathFinder(entity);
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
			mc.gameSettings.keyBindForward.pressed = WMinecraft.getPlayer()
				.getDistanceToEntity(entity) > distance.getValueF();
			if(!RotationUtils.faceEntityClient(entity))
				return;
		}
		
		// check timer / cooldown
		if(useCooldown != null && useCooldown.isChecked()
			? WPlayer.getCooldown() < 1 : !hasTimePassedS(speed.getValueF()))
			return;
		
		// check range
		if(!EntityUtils.isCorrectEntity(entity, attackSettings))
			return;
		
		// attack entity
		WPlayer.prepareAttack();
		WPlayer.attackEntity(entity);
		
		// reset timer
		updateLastMS();
	}
	
	@Override
	public void onRender(float partialTicks)
	{
		PathCmd pathCmd = wurst.commands.pathCmd;
		pathFinder.renderPath(pathCmd.isDebugMode(), pathCmd.isDepthTest());
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
	
	private class EntityPathFinder extends PathFinder
	{
		private final Entity entity;
		
		public EntityPathFinder(Entity entity)
		{
			super(new BlockPos(entity));
			this.entity = entity;
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
