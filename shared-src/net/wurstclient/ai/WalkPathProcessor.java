/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.ai;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockVine;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.wurstclient.compatibility.WBlock;
import net.wurstclient.compatibility.WMath;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.utils.RotationUtils;

public class WalkPathProcessor extends PathProcessor
{
	public WalkPathProcessor(ArrayList<PathPos> path)
	{
		super(path);
	}
	
	@Override
	public void process()
	{
		// get positions
		BlockPos pos;
		if(WMinecraft.getPlayer().onGround)
			pos = new BlockPos(WMinecraft.getPlayer().posX,
				WMinecraft.getPlayer().posY + 0.5, WMinecraft.getPlayer().posZ);
		else
			pos = new BlockPos(WMinecraft.getPlayer());
		PathPos nextPos = path.get(index);
		int posIndex = path.indexOf(pos);
		
		if(posIndex == -1)
			ticksOffPath++;
		else
			ticksOffPath = 0;
		
		// update index
		if(pos.equals(nextPos))
		{
			index++;
			
			// disable when done
			if(index >= path.size())
				done = true;
			return;
		}else if(posIndex > index)
		{
			index = posIndex + 1;
			
			// disable when done
			if(index >= path.size())
				done = true;
			return;
		}
		
		lockControls();
		WMinecraft.getPlayer().capabilities.isFlying = false;
		
		// face next position
		facePosition(nextPos);
		if(WMath.wrapDegrees(
			Math.abs(RotationUtils.getHorizontalAngleToClientRotation(
				new Vec3d(nextPos).addVector(0.5, 0.5, 0.5)))) > 90)
			return;
		
		if(wurst.mods.jesusMod.isActive())
		{
			// wait for Jesus to swim up
			if(WMinecraft.getPlayer().posY < nextPos.getY()
				&& (WMinecraft.getPlayer().isInWater()
					|| WMinecraft.getPlayer().isInLava()))
				return;
			
			// manually swim down if using Jesus
			if(WMinecraft.getPlayer().posY - nextPos.getY() > 0.5
				&& (WMinecraft.getPlayer().isInWater()
					|| WMinecraft.getPlayer().isInLava()
					|| wurst.mods.jesusMod.isOverLiquid()))
				mc.gameSettings.keyBindSneak.pressed = true;
		}
		
		// horizontal movement
		if(pos.getX() != nextPos.getX() || pos.getZ() != nextPos.getZ())
		{
			mc.gameSettings.keyBindForward.pressed = true;
			
			if(index > 0 && path.get(index - 1).isJumping()
				|| pos.getY() < nextPos.getY())
				mc.gameSettings.keyBindJump.pressed = true;
			
			// vertical movement
		}else if(pos.getY() != nextPos.getY())
			// go up
			if(pos.getY() < nextPos.getY())
			{
				// climb up
				// TODO: Spider
				Block block = WBlock.getBlock(pos);
				if(block instanceof BlockLadder || block instanceof BlockVine)
				{
					RotationUtils.faceVectorForWalking(
						WBlock.getBoundingBox(pos).getCenter());
					
					mc.gameSettings.keyBindForward.pressed = true;
					
				}else
				{
					// directional jump
					if(index < path.size() - 1
						&& !nextPos.up().equals(path.get(index + 1)))
						index++;
					
					// jump up
					mc.gameSettings.keyBindJump.pressed = true;
				}
				
				// go down
			}else
			{
				// skip mid-air nodes and go straight to the bottom
				while(index < path.size() - 1
					&& path.get(index).down().equals(path.get(index + 1)))
					index++;
				
				// walk off the edge
				if(WMinecraft.getPlayer().onGround)
					mc.gameSettings.keyBindForward.pressed = true;
			}
	}
}
