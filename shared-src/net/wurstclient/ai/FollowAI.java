/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.ai;

import net.minecraft.entity.Entity;

public class FollowAI
{
	private EntityPathFinder pathFinder;
	private PathProcessor processor;
	
	private boolean done;
	private boolean failed;
	
	public FollowAI(Entity entity, float distance)
	{
		pathFinder = new EntityPathFinder(entity, distance);
		pathFinder.setThinkTime(40);
	}
	
	public void update()
	{
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
			pathFinder = new EntityPathFinder(pathFinder);
			return;
		}
		
		// process path
		if(!processor.isFailed() && !processor.isDone())
			processor.process();
		else
		{
			processor.lockControls();
			
			if(processor.isFailed())
				failed = true;
			else if(processor.isDone())
				done = true;
		}
		
	}
	
	public void stop()
	{
		if(processor != null)
			processor.stop();
	}
	
	public final boolean isDone()
	{
		return done;
	}
	
	public final boolean isFailed()
	{
		return failed;
	}
}
