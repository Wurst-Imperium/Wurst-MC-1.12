/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.commands;

import net.minecraft.util.math.BlockPos;
import net.wurstclient.ai.PathFinder;
import net.wurstclient.ai.PathProcessor;
import net.wurstclient.events.listeners.RenderListener;
import net.wurstclient.events.listeners.UpdateListener;
import net.wurstclient.features.Cmd;
import net.wurstclient.features.HelpPage;
import net.wurstclient.utils.ChatUtils;
import net.wurstclient.utils.EntityUtils.TargetSettings;

@HelpPage("Commands/goto")
public final class GoToCmd extends Cmd implements UpdateListener, RenderListener
{
	private PathFinder pathFinder;
	private PathProcessor processor;
	private boolean enabled;
	
	private TargetSettings targetSettings = new TargetSettings()
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
	};
	
	public GoToCmd()
	{
		super("goto", "Walks or flies you to a specific location.",
			"<x> <y> <z>", "<entity>", "-path");
	}
	
	@Override
	public void execute(String[] args) throws CmdError
	{
		// disable if enabled
		if(enabled)
		{
			disable();
			
			if(args.length == 0)
				return;
		}
		
		// set PathFinder
		if(args.length == 1 && args[0].equals("-path"))
		{
			BlockPos goal = wurst.commands.pathCmd.getLastGoal();
			if(goal != null)
				pathFinder = new PathFinder(goal);
			else
				error("No previous position on .path.");
		}else
		{
			int[] goal = argsToPos(targetSettings, args);
			pathFinder =
				new PathFinder(new BlockPos(goal[0], goal[1], goal[2]));
		}
		
		// start
		enabled = true;
		wurst.events.add(UpdateListener.class, this);
		wurst.events.add(RenderListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		// find path
		if(!pathFinder.isDone())
		{
			PathProcessor.lockControls();
			
			pathFinder.think();
			
			if(!pathFinder.isDone())
			{
				if(pathFinder.isFailed())
				{
					ChatUtils.error("Could not find a path.");
					disable();
				}
				
				return;
			}
			
			pathFinder.formatPath();
			
			// set processor
			processor = pathFinder.getProcessor();
			
			System.out.println("Done");
		}
		
		// check path
		if(processor != null
			&& !pathFinder.isPathStillValid(processor.getIndex()))
		{
			System.out.println("Updating path...");
			pathFinder = new PathFinder(pathFinder.getGoal());
			return;
		}
		
		// process path
		processor.process();
		
		if(processor.isDone())
			disable();
	}
	
	@Override
	public void onRender(float partialTicks)
	{
		PathCmd pathCmd = wurst.commands.pathCmd;
		pathFinder.renderPath(pathCmd.isDebugMode(), pathCmd.isDepthTest());
	}
	
	private void disable()
	{
		wurst.events.remove(UpdateListener.class, this);
		wurst.events.remove(RenderListener.class, this);
		
		pathFinder = null;
		processor = null;
		PathProcessor.releaseControls();
		
		enabled = false;
	}
	
	public boolean isActive()
	{
		return enabled;
	}
}
