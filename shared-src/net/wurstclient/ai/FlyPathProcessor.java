/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.ai;

import java.util.ArrayList;

import net.minecraft.util.math.BlockPos;
import net.wurstclient.compatibility.WMath;
import net.wurstclient.compatibility.WMinecraft;

public class FlyPathProcessor extends PathProcessor
{
	private final boolean creativeFlying;
	private boolean stopped;
	
	public FlyPathProcessor(ArrayList<PathPos> path, boolean creativeFlying)
	{
		super(path);
		this.creativeFlying = creativeFlying;
	}
	
	@Override
	public void process()
	{
		// get positions
		BlockPos pos = new BlockPos(WMinecraft.getPlayer());
		BlockPos nextPos = path.get(index);
		
		// update index
		if(pos.equals(nextPos))
		{
			index++;
			
			if(index < path.size())
			{
				// stop when changing directions
				if(creativeFlying && index >= 2)
				{
					BlockPos prevPos = path.get(index - 1);
					if(!path.get(index).subtract(prevPos)
						.equals(prevPos.subtract(path.get(index - 2))))
						if(!stopped)
						{
							WMinecraft.getPlayer().motionX /= Math.max(
								Math.abs(WMinecraft.getPlayer().motionX) * 50,
								1);
							WMinecraft.getPlayer().motionY /= Math.max(
								Math.abs(WMinecraft.getPlayer().motionY) * 50,
								1);
							WMinecraft.getPlayer().motionZ /= Math.max(
								Math.abs(WMinecraft.getPlayer().motionZ) * 50,
								1);
							stopped = true;
						}
				}
				
				// disable when done
			}else
			{
				if(creativeFlying)
				{
					WMinecraft.getPlayer().motionX /= Math
						.max(Math.abs(WMinecraft.getPlayer().motionX) * 50, 1);
					WMinecraft.getPlayer().motionY /= Math
						.max(Math.abs(WMinecraft.getPlayer().motionY) * 50, 1);
					WMinecraft.getPlayer().motionZ /= Math
						.max(Math.abs(WMinecraft.getPlayer().motionZ) * 50, 1);
				}
				
				done = true;
			}
			
			return;
		}
		
		stopped = false;
		
		lockControls();
		
		// face next position
		facePosition(nextPos);
		
		// limit vertical speed
		WMinecraft.getPlayer().motionY =
			WMath.clamp(WMinecraft.getPlayer().motionY, -0.25, 0.25);
		
		// horizontal movement
		if(pos.getX() != nextPos.getX() || pos.getZ() != nextPos.getZ())
		{
			mc.gameSettings.keyBindForward.pressed = true;
			
			if(WMinecraft.getPlayer().isCollidedHorizontally)
				if(WMinecraft.getPlayer().posY > nextPos.getY() + 0.2)
					mc.gameSettings.keyBindSneak.pressed = true;
				else if(WMinecraft.getPlayer().posY < nextPos.getY())
					mc.gameSettings.keyBindJump.pressed = true;
				
			// vertical movement
		}else if(pos.getY() != nextPos.getY())
		{
			if(pos.getY() < nextPos.getY())
				mc.gameSettings.keyBindJump.pressed = true;
			else
				mc.gameSettings.keyBindSneak.pressed = true;
			
			if(WMinecraft.getPlayer().isCollidedVertically)
			{
				mc.gameSettings.keyBindSneak.pressed = false;
				mc.gameSettings.keyBindForward.pressed = true;
			}
		}
	}
	
	@Override
	public void lockControls()
	{
		super.lockControls();
		WMinecraft.getPlayer().capabilities.isFlying = creativeFlying;
	}
}
