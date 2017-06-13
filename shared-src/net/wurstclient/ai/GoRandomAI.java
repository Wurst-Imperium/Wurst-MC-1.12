/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.ai;

import net.minecraft.util.math.BlockPos;

public class GoRandomAI
{
	private RandomPathFinder pathFinder;
	private PathProcessor processor;
	
	private boolean done;
	
	public GoRandomAI(BlockPos start, float range)
	{
		pathFinder = new RandomPathFinder(start, range);
		pathFinder.setThinkTime(10);
		pathFinder.setFallingAllowed(false);
	}
	
	public void update()
	{
		done = false;
		
		// find path
		if(!pathFinder.isDone() && !pathFinder.isFailed())
		{
			if(processor != null)
				processor.lockControls();
			
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
		if(!processor.isFailed() && !processor.isDone())
			processor.process();
		else
			pathFinder = new RandomPathFinder(pathFinder);
		
		if(processor.isDone())
			done = true;
	}
	
	public final boolean isDone()
	{
		return done;
	}
	
	public void stop()
	{
		if(processor != null)
			processor.stop();
	}
}
