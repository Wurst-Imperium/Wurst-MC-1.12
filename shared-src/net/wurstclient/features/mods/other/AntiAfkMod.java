/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.other;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.wurstclient.ai.PathFinder;
import net.wurstclient.ai.PathPos;
import net.wurstclient.ai.PathProcessor;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.RenderListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;
import net.wurstclient.features.commands.PathCmd;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.utils.RotationUtils;

@SearchTags({"AFKBot", "anti afk", "afk bot"})
@Mod.Bypasses(ghostMode = false)
@Mod.DontSaveState
public final class AntiAfkMod extends Mod
	implements UpdateListener, RenderListener
{
	private final CheckboxSetting useAi = new CheckboxSetting("Use AI", true);
	
	private int timer;
	private Random random = new Random();
	private BlockPos start;
	private BlockPos nextBlock;
	
	private RandomPathFinder pathFinder;
	private PathProcessor processor;
	private boolean creativeFlying;
	
	public AntiAfkMod()
	{
		super("AntiAFK",
			"Walks around randomly to hide you from AFK detectors.\n"
				+ "Needs 3x3 blocks of free space.");
		setCategory(Category.OTHER);
	}
	
	@Override
	public void initSettings()
	{
		addSetting(useAi);
	}
	
	@Override
	public void onEnable()
	{
		start = new BlockPos(WMinecraft.getPlayer());
		nextBlock = null;
		pathFinder = new RandomPathFinder(start);
		creativeFlying = WMinecraft.getPlayer().capabilities.isFlying;
		
		wurst.events.add(UpdateListener.class, this);
		wurst.events.add(RenderListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
		wurst.events.remove(RenderListener.class, this);
		
		mc.gameSettings.keyBindForward.pressed =
			GameSettings.isKeyDown(mc.gameSettings.keyBindForward);
		mc.gameSettings.keyBindJump.pressed =
			GameSettings.isKeyDown(mc.gameSettings.keyBindJump);
		
		pathFinder = null;
		processor = null;
		PathProcessor.releaseControls();
	}
	
	@Override
	public void onUpdate()
	{
		// check if player died
		if(WMinecraft.getPlayer().getHealth() <= 0)
		{
			setEnabled(false);
			return;
		}
		
		WMinecraft.getPlayer().capabilities.isFlying = creativeFlying;
		
		if(useAi.isChecked())
		{
			// update timer
			if(timer > 0)
			{
				timer--;
				if(!wurst.mods.jesusMod.isActive())
					mc.gameSettings.keyBindJump.pressed =
						WMinecraft.getPlayer().isInWater();
				return;
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
				pathFinder = new RandomPathFinder(pathFinder);
				return;
			}
			
			// process path
			if(!processor.isDone())
				processor.process();
			else
				pathFinder = new RandomPathFinder(start);
			
			// wait 2 - 3 seconds (40 - 60 ticks)
			if(processor.isDone())
			{
				PathProcessor.releaseControls();
				timer = 40 + random.nextInt(21);
			}
		}else
		{
			// set next block
			if(timer <= 0 || nextBlock == null)
			{
				nextBlock =
					start.add(random.nextInt(3) - 1, 0, random.nextInt(3) - 1);
				timer = 40 + random.nextInt(21);
			}
			
			// face block
			RotationUtils.faceVectorForWalking(
				new Vec3d(nextBlock).addVector(0.5, 0.5, 0.5));
			
			// walk
			if(WMinecraft.getPlayer().getDistanceSqToCenter(nextBlock) > 0.5)
				mc.gameSettings.keyBindForward.pressed = true;
			else
				mc.gameSettings.keyBindForward.pressed = false;
			
			// swim up
			mc.gameSettings.keyBindJump.pressed =
				WMinecraft.getPlayer().isInWater();
			
			// update timer
			if(timer > 0)
				timer--;
		}
	}
	
	@Override
	public void onRender(float partialTicks)
	{
		if(!useAi.isChecked())
			return;
		
		PathCmd pathCmd = wurst.commands.pathCmd;
		pathFinder.renderPath(pathCmd.isDebugMode(), pathCmd.isDepthTest());
	}
	
	private class RandomPathFinder extends PathFinder
	{
		public RandomPathFinder(BlockPos goal)
		{
			super(goal.add(random.nextInt(33) - 16, random.nextInt(33) - 16,
				random.nextInt(33) - 16));
			setThinkTime(10);
			setFallingAllowed(false);
			setDivingAllowed(false);
		}
		
		public RandomPathFinder(PathFinder pathFinder)
		{
			super(pathFinder);
			setFallingAllowed(false);
			setDivingAllowed(false);
		}
		
		@Override
		public ArrayList<PathPos> formatPath()
		{
			failed = true;
			return super.formatPath();
		}
	}
}
