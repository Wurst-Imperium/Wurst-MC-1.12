/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.commands;

import java.util.ArrayList;

import net.minecraft.util.math.BlockPos;
import net.wurstclient.ai.PathFinder;
import net.wurstclient.ai.PathPos;
import net.wurstclient.events.RenderListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Cmd;
import net.wurstclient.features.HelpPage;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.utils.ChatUtils;

@HelpPage("Commands/path")
public final class PathCmd extends Cmd implements UpdateListener, RenderListener
{
	private final CheckboxSetting debugMode =
		new CheckboxSetting("Debug mode", false);
	private final CheckboxSetting depthTest =
		new CheckboxSetting("Depth test", false);
	
	private PathFinder pathFinder;
	private boolean enabled;
	private long startTime;
	private BlockPos lastGoal;
	
	public PathCmd()
	{
		super("path",
			"Shows the shortest path to a specific point. Useful for labyrinths and caves.",
			"<x> <y> <z>", "<entity>", "-debug", "-depth", "-refresh");
		addSetting(debugMode);
		addSetting(depthTest);
	}
	
	@Override
	public void call(String[] args) throws CmdException
	{
		// process special commands
		boolean refresh = false;
		if(args.length > 0 && args[0].startsWith("-"))
			switch(args[0])
			{
				case "-debug":
				debugMode.toggle();
				ChatUtils.message("Debug mode "
					+ (debugMode.isChecked() ? "on" : "off") + ".");
				return;
				case "-depth":
				depthTest.toggle();
				ChatUtils.message("Depth test "
					+ (depthTest.isChecked() ? "on" : "off") + ".");
				return;
				case "-refresh":
				if(lastGoal == null)
					throw new CmdError("Cannot refresh: no previous path.");
				refresh = true;
				break;
			}
		
		// disable if enabled
		if(enabled)
		{
			wurst.events.remove(UpdateListener.class, this);
			wurst.events.remove(RenderListener.class, this);
			enabled = false;
			
			if(args.length == 0)
				return;
		}
		
		// set PathFinder
		final BlockPos goal;
		if(refresh)
			goal = lastGoal;
		else
		{
			goal = argsToPos(args);
			lastGoal = goal;
		}
		pathFinder = new PathFinder(goal);
		
		// start
		enabled = true;
		wurst.events.add(UpdateListener.class, this);
		wurst.events.add(RenderListener.class, this);
		System.out.println("Finding path...");
		startTime = System.nanoTime();
	}
	
	@Override
	public void onUpdate()
	{
		double passedTime = (System.nanoTime() - startTime) / 1e6;
		pathFinder.think();
		boolean foundPath = pathFinder.isDone();
		
		// stop if done or failed
		if(foundPath || pathFinder.isFailed())
		{
			ArrayList<PathPos> path = new ArrayList<>();
			if(foundPath)
				path = pathFinder.formatPath();
			else
				ChatUtils.error("Could not find a path.");
			
			wurst.events.remove(UpdateListener.class, this);
			
			System.out.println("Done after " + passedTime + "ms");
			if(debugMode.isChecked())
				System.out.println("Length: " + path.size() + ", processed: "
					+ pathFinder.countProcessedBlocks() + ", queue: "
					+ pathFinder.getQueueSize() + ", cost: "
					+ pathFinder.getCost(pathFinder.getCurrentPos()));
		}
	}
	
	@Override
	public void onRender(float partialTicks)
	{
		pathFinder.renderPath(debugMode.isChecked(), depthTest.isChecked());
	}
	
	public BlockPos getLastGoal()
	{
		return lastGoal;
	}
	
	public boolean isDebugMode()
	{
		return debugMode.isChecked();
	}
	
	public boolean isDepthTest()
	{
		return depthTest.isChecked();
	}
}
