/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.ai;

import java.util.ArrayList;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.wurstclient.compatibility.WMath;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.utils.RotationUtils;

public class FlyPathProcessor extends PathProcessor
{
	private final boolean creativeFlying;
	
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
		Vec3d posVec = WMinecraft.getPlayer().getPositionVector();
		BlockPos nextPos = path.get(index);
		int posIndex = path.indexOf(pos);
		AxisAlignedBB nextBox = new AxisAlignedBB(nextPos.getX() + 0.3,
			nextPos.getY(), nextPos.getZ() + 0.3, nextPos.getX() + 0.7,
			nextPos.getY() + 0.2, nextPos.getZ() + 0.7);
		
		if(posIndex == -1)
			ticksOffPath++;
		else
			ticksOffPath = 0;
		
		// update index
		if(posIndex > index || posVec.xCoord >= nextBox.minX
			&& posVec.xCoord <= nextBox.maxX && posVec.yCoord >= nextBox.minY
			&& posVec.yCoord <= nextBox.maxY && posVec.zCoord >= nextBox.minZ
			&& posVec.zCoord <= nextBox.maxZ)
		{
			if(posIndex > index)
				index = posIndex + 1;
			else
				index++;
			
			// stop when changing directions
			if(creativeFlying)
			{
				WMinecraft.getPlayer().motionX /=
					Math.max(Math.abs(WMinecraft.getPlayer().motionX) * 50, 1);
				WMinecraft.getPlayer().motionY /=
					Math.max(Math.abs(WMinecraft.getPlayer().motionY) * 50, 1);
				WMinecraft.getPlayer().motionZ /=
					Math.max(Math.abs(WMinecraft.getPlayer().motionZ) * 50, 1);
			}
			
			if(index >= path.size())
				done = true;
			
			return;
		}
		
		lockControls();
		WMinecraft.getPlayer().capabilities.isFlying = creativeFlying;
		boolean x =
			posVec.xCoord < nextBox.minX || posVec.xCoord > nextBox.maxX;
		boolean y =
			posVec.yCoord < nextBox.minY || posVec.yCoord > nextBox.maxY;
		boolean z =
			posVec.zCoord < nextBox.minZ || posVec.zCoord > nextBox.maxZ;
		boolean horizontal = x || z;
		
		// face next position
		if(horizontal)
		{
			facePosition(nextPos);
			if(Math.abs(WMath
				.wrapDegrees(RotationUtils.getHorizontalAngleToClientRotation(
					new Vec3d(nextPos).addVector(0.5, 0.5, 0.5)))) > 1)
				return;
		}
		
		// skip mid-air nodes
		Vec3i offset = nextPos.subtract(pos);
		while(index < path.size() - 1
			&& path.get(index).add(offset).equals(path.get(index + 1)))
			index++;
		
		if(creativeFlying)
		{
			if(!x)
				WMinecraft.getPlayer().motionX /=
					Math.max(Math.abs(WMinecraft.getPlayer().motionX) * 50, 1);
			if(!y)
				WMinecraft.getPlayer().motionY /=
					Math.max(Math.abs(WMinecraft.getPlayer().motionY) * 50, 1);
			if(!z)
				WMinecraft.getPlayer().motionZ /=
					Math.max(Math.abs(WMinecraft.getPlayer().motionZ) * 50, 1);
		}
		
		// horizontal movement
		if(horizontal)
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
				if(posVec.yCoord > nextBox.maxY)
					mc.gameSettings.keyBindSneak.pressed = true;
				else if(posVec.yCoord < nextBox.minY)
					mc.gameSettings.keyBindJump.pressed = true;
				
			// vertical movement
		}else if(y)
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
			
			if(posVec.yCoord < nextBox.minY)
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
