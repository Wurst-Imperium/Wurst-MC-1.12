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
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.utils.RotationUtils;

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
		int posIndex = path.indexOf(pos);
		
		// update index
		if(pos.equals(nextPos) || posIndex > index)
		{
			if(pos.equals(nextPos))
				index++;
			else
				index = posIndex + 1;
			
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
		WMinecraft.getPlayer().capabilities.isFlying = creativeFlying;
		
		// face next position
		facePosition(nextPos);
		if(Math.abs(RotationUtils.getHorizontalAngleToClientRotation(
			new Vec3d(nextPos).addVector(0.5, 0.5, 0.5))) > 1)
			return;
		
		// skip mid-air nodes
		Vec3i offset = nextPos.subtract(pos);
		while(index < path.size() - 1
			&& path.get(index).add(offset).equals(path.get(index + 1)))
			index++;
		
		// horizontal movement
		if(pos.getX() != nextPos.getX() || pos.getZ() != nextPos.getZ())
		{
			if(!creativeFlying && WMinecraft.getPlayer().getDistance(
				nextPos.getX() + 0.5, pos.getY() + 0.1,
				nextPos.getZ() + 0.5) <= wurst.mods.flightMod.speed.getValueF())
			{
				WMinecraft.getPlayer().setPosition(nextPos.getX() + 0.5,
					pos.getY() + 0.1, nextPos.getZ() + 0.5);
				return;
			}
			
			mc.gameSettings.keyBindForward.pressed = true;
			
			if(WMinecraft.getPlayer().isCollidedHorizontally)
				if(WMinecraft.getPlayer().posY > nextPos.getY() + 0.2)
					mc.gameSettings.keyBindSneak.pressed = true;
				else if(WMinecraft.getPlayer().posY < nextPos.getY())
					mc.gameSettings.keyBindJump.pressed = true;
				
			// vertical movement
		}else if(pos.getY() != nextPos.getY())
		{
			if(!creativeFlying
				&& WMinecraft.getPlayer().getDistance(pos.getX() + 0.5,
					nextPos.getY() + 0.1,
					pos.getZ() + 0.5) <= wurst.mods.flightMod.speed.getValueF())
			{
				WMinecraft.getPlayer().setPosition(pos.getX() + 0.5,
					nextPos.getY() + 0.1, pos.getZ() + 0.5);
				return;
			}
			
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
}
