/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.ai;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import net.minecraft.block.BlockLiquid;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.compatibility.WBlock;
import net.wurstclient.compatibility.WMinecraft;

public class RandomPathFinder extends PathFinder
{
	private final BlockPos center;
	private final float rangeSq;
	private final int blockRange;
	private final Random random;
	
	public RandomPathFinder(BlockPos center, float range)
	{
		super(new BlockPos(WMinecraft.getPlayer()));
		this.center = center;
		rangeSq = (float)Math.pow(range, 2);
		blockRange = (int)Math.ceil(range);
		random = new Random();
	}
	
	public RandomPathFinder(RandomPathFinder pathFinder)
	{
		super(new BlockPos(WMinecraft.getPlayer()));
		thinkSpeed = pathFinder.thinkSpeed;
		thinkTime = pathFinder.thinkTime;
		fallingAllowed = pathFinder.fallingAllowed;
		center = pathFinder.center;
		rangeSq = pathFinder.rangeSq;
		blockRange = pathFinder.blockRange;
		random = pathFinder.random;
	}
	
	@Override
	protected boolean isPassable(BlockPos pos)
	{
		if(Math.abs(center.getX() - pos.getX()) > blockRange)
			return false;
		if(Math.abs(center.getY() - pos.getY()) > blockRange)
			return false;
		if(Math.abs(center.getZ() - pos.getZ()) > blockRange)
			return false;
		
		return super.isPassable(pos);
	}
	
	@Override
	protected boolean checkDone()
	{
		return false;
	}
	
	private void setCurrentToRandomNode()
	{
		int currentIndex = 0;
		int randomIndex = random.nextInt(prevPosMap.size());
		
		for(Iterator<PathPos> itr = prevPosMap.keySet().iterator(); itr
			.hasNext(); currentIndex++)
			if(currentIndex == randomIndex)
			{
				current = itr.next();
				break;
			}else
				itr.next();
	}
	
	@Override
	public ArrayList<PathPos> formatPath()
	{
		done = true;
		failed = false;
		
		do
			setCurrentToRandomNode();
		while(center.distanceSq(current) > rangeSq
			|| !flying && !canBeSolid(current.down())
			|| WBlock.getBlock(current.up()) instanceof BlockLiquid);
		
		return super.formatPath();
	}
}
