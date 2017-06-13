/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.ai;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

public class EntityPathFinder extends PathFinder
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
