/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.utils;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashSet;

import com.google.common.collect.AbstractIterator;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerDigging.Action;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.wurstclient.WurstClient;
import net.wurstclient.compatibility.WBlock;
import net.wurstclient.compatibility.WConnection;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.compatibility.WPlayer;
import net.wurstclient.compatibility.WPlayerController;

public final class BlockUtils
{
	private static final Minecraft mc = Minecraft.getMinecraft();
	
	public static boolean placeBlockLegit(BlockPos pos)
	{
		Vec3d eyesPos = RotationUtils.getEyesPos();
		Vec3d posVec = new Vec3d(pos).addVector(0.5, 0.5, 0.5);
		double distanceSqPosVec = eyesPos.squareDistanceTo(posVec);
		
		for(EnumFacing side : EnumFacing.values())
		{
			BlockPos neighbor = pos.offset(side);
			
			// check if neighbor can be right clicked
			if(!WBlock.canBeClicked(neighbor))
				continue;
			
			Vec3d dirVec = new Vec3d(side.getDirectionVec());
			Vec3d hitVec = posVec.add(dirVec.scale(0.5));
			
			// check if hitVec is within range (4.25 blocks)
			if(eyesPos.squareDistanceTo(hitVec) > 18.0625)
				continue;
			
			// check if side is visible (facing away from player)
			if(distanceSqPosVec > eyesPos.squareDistanceTo(posVec.add(dirVec)))
				continue;
			
			// check line of sight
			if(WMinecraft.getWorld().rayTraceBlocks(eyesPos, hitVec, false,
				true, false) != null)
				continue;
			
			// face block
			RotationUtils.faceVectorPacketInstant(hitVec);
			
			// place block
			WPlayerController.processRightClickBlock(neighbor,
				side.getOpposite(), hitVec);
			WPlayer.swingArmClient();
			mc.rightClickDelayTimer = 4;
			
			return true;
		}
		
		return false;
	}
	
	public static boolean placeBlockScaffold(BlockPos pos)
	{
		Vec3d eyesPos = new Vec3d(WMinecraft.getPlayer().posX,
			WMinecraft.getPlayer().posY + WMinecraft.getPlayer().getEyeHeight(),
			WMinecraft.getPlayer().posZ);
		
		for(EnumFacing side : EnumFacing.values())
		{
			BlockPos neighbor = pos.offset(side);
			EnumFacing side2 = side.getOpposite();
			
			// check if side is visible (facing away from player)
			if(eyesPos.squareDistanceTo(
				new Vec3d(pos).addVector(0.5, 0.5, 0.5)) >= eyesPos
					.squareDistanceTo(
						new Vec3d(neighbor).addVector(0.5, 0.5, 0.5)))
				continue;
			
			// check if neighbor can be right clicked
			if(!WBlock.canBeClicked(neighbor))
				continue;
			
			Vec3d hitVec = new Vec3d(neighbor).addVector(0.5, 0.5, 0.5)
				.add(new Vec3d(side2.getDirectionVec()).scale(0.5));
			
			// check if hitVec is within range (4.25 blocks)
			if(eyesPos.squareDistanceTo(hitVec) > 18.0625)
				continue;
			
			// place block
			RotationUtils.faceVectorPacketInstant(hitVec);
			WPlayerController.processRightClickBlock(neighbor, side2, hitVec);
			WPlayer.swingArmClient();
			mc.rightClickDelayTimer = 4;
			
			return true;
		}
		
		return false;
	}
	
	public static void placeBlockSimple(BlockPos pos)
	{
		EnumFacing side = null;
		EnumFacing[] sides = EnumFacing.values();
		
		Vec3d eyesPos = RotationUtils.getEyesPos();
		Vec3d posVec = new Vec3d(pos).addVector(0.5, 0.5, 0.5);
		double distanceSqPosVec = eyesPos.squareDistanceTo(posVec);
		
		Vec3d[] hitVecs = new Vec3d[sides.length];
		for(int i = 0; i < sides.length; i++)
			hitVecs[i] =
				posVec.add(new Vec3d(sides[i].getDirectionVec()).scale(0.5));
		
		for(int i = 0; i < sides.length; i++)
		{
			// check if neighbor can be right clicked
			if(!WBlock.canBeClicked(pos.offset(sides[i])))
				continue;
			
			// check line of sight
			if(WMinecraft.getWorld().rayTraceBlocks(eyesPos, hitVecs[i], false,
				true, false) != null)
				continue;
			
			side = sides[i];
			break;
		}
		
		if(side == null)
			for(int i = 0; i < sides.length; i++)
			{
				// check if neighbor can be right clicked
				if(!WBlock.canBeClicked(pos.offset(sides[i])))
					continue;
				
				// check if side is facing away from player
				if(distanceSqPosVec > eyesPos.squareDistanceTo(hitVecs[i]))
					continue;
				
				side = sides[i];
				break;
			}
		
		if(side == null)
			return;
		
		Vec3d hitVec = hitVecs[side.ordinal()];
		
		// face block
		RotationUtils.faceVectorPacket(hitVec);
		if(RotationUtils.getAngleToLastReportedLookVec(hitVec) > 1)
			return;
		
		// check timer
		if(mc.rightClickDelayTimer > 0)
			return;
		
		// place block
		WPlayerController.processRightClickBlock(pos.offset(side),
			side.getOpposite(), hitVec);
		
		// swing arm
		WPlayer.swingArmPacket();
		
		// reset timer
		mc.rightClickDelayTimer = 4;
	}
	
	public static boolean placeBlockSimple_old(BlockPos pos)
	{
		Vec3d eyesPos = RotationUtils.getEyesPos();
		Vec3d posVec = new Vec3d(pos).addVector(0.5, 0.5, 0.5);
		
		for(EnumFacing side : EnumFacing.values())
		{
			BlockPos neighbor = pos.offset(side);
			
			// check if neighbor can be right clicked
			if(!WBlock.canBeClicked(neighbor))
				continue;
			
			Vec3d hitVec =
				posVec.add(new Vec3d(side.getDirectionVec()).scale(0.5));
			
			// check if hitVec is within range (6 blocks)
			if(eyesPos.squareDistanceTo(hitVec) > 36)
				continue;
			
			// place block
			WPlayerController.processRightClickBlock(neighbor,
				side.getOpposite(), hitVec);
			
			return true;
		}
		
		return false;
	}
	
	public static boolean prepareToBreakBlockLegit(BlockPos pos)
	{
		Vec3d eyesPos = RotationUtils.getEyesPos();
		Vec3d posVec = new Vec3d(pos).addVector(0.5, 0.5, 0.5);
		double distanceSqPosVec = eyesPos.squareDistanceTo(posVec);
		
		for(EnumFacing side : EnumFacing.values())
		{
			Vec3d hitVec =
				posVec.add(new Vec3d(side.getDirectionVec()).scale(0.5));
			double distanceSqHitVec = eyesPos.squareDistanceTo(hitVec);
			
			// check if hitVec is within range (4.25 blocks)
			if(distanceSqHitVec > 18.0625)
				continue;
			
			// check if side is facing towards player
			if(distanceSqHitVec >= distanceSqPosVec)
				continue;
			
			// check line of sight
			if(WMinecraft.getWorld().rayTraceBlocks(eyesPos, hitVec, false,
				true, false) != null)
				continue;
			
			// AutoTool
			WurstClient.INSTANCE.mods.autoToolMod.setSlot(pos);
			
			// face block
			if(!RotationUtils.faceVectorPacket(hitVec))
				return true;
			
			return true;
		}
		
		return false;
	}
	
	public static boolean breakBlockLegit(BlockPos pos)
	{
		Vec3d eyesPos = RotationUtils.getEyesPos();
		Vec3d posVec = new Vec3d(pos).addVector(0.5, 0.5, 0.5);
		double distanceSqPosVec = eyesPos.squareDistanceTo(posVec);
		
		for(EnumFacing side : EnumFacing.values())
		{
			Vec3d hitVec =
				posVec.add(new Vec3d(side.getDirectionVec()).scale(0.5));
			double distanceSqHitVec = eyesPos.squareDistanceTo(hitVec);
			
			// check if hitVec is within range (4.25 blocks)
			if(distanceSqHitVec > 18.0625)
				continue;
			
			// check if side is facing towards player
			if(distanceSqHitVec >= distanceSqPosVec)
				continue;
			
			// check line of sight
			if(WMinecraft.getWorld().rayTraceBlocks(eyesPos, hitVec, false,
				true, false) != null)
				continue;
			
			// damage block
			if(!mc.playerController.onPlayerDamageBlock(pos, side))
				return false;
			
			// swing arm
			WPlayer.swingArmPacket();
			
			return true;
		}
		
		return false;
	}
	
	public static boolean breakBlockExtraLegit(BlockPos pos)
	{
		Vec3d eyesPos = RotationUtils.getEyesPos();
		Vec3d posVec = new Vec3d(pos).addVector(0.5, 0.5, 0.5);
		double distanceSqPosVec = eyesPos.squareDistanceTo(posVec);
		
		for(EnumFacing side : EnumFacing.values())
		{
			Vec3d hitVec =
				posVec.add(new Vec3d(side.getDirectionVec()).scale(0.5));
			double distanceSqHitVec = eyesPos.squareDistanceTo(hitVec);
			
			// check if hitVec is within range (4.25 blocks)
			if(distanceSqHitVec > 18.0625)
				continue;
			
			// check if side is facing towards player
			if(distanceSqHitVec >= distanceSqPosVec)
				continue;
			
			// check line of sight
			if(WMinecraft.getWorld().rayTraceBlocks(eyesPos, hitVec, false,
				true, false) != null)
				continue;
			
			// AutoTool
			WurstClient.INSTANCE.mods.autoToolMod.setSlot(pos);
			
			// face block
			if(!RotationUtils.faceVectorClient(hitVec))
				return true;
				
			// if attack key is down but nothing happens, release it for one
			// tick
			if(mc.gameSettings.keyBindAttack.pressed
				&& !mc.playerController.getIsHittingBlock())
			{
				mc.gameSettings.keyBindAttack.pressed = false;
				return true;
			}
			
			// damage block
			mc.gameSettings.keyBindAttack.pressed = true;
			
			return true;
		}
		
		return false;
	}
	
	public static boolean breakBlockSimple(BlockPos pos)
	{
		EnumFacing side = null;
		EnumFacing[] sides = EnumFacing.values();
		
		Vec3d eyesPos = RotationUtils.getEyesPos();
		Vec3d relCenter = WBlock.getBoundingBox(pos)
			.offset(-pos.getX(), -pos.getY(), -pos.getZ()).getCenter();
		Vec3d center = new Vec3d(pos).add(relCenter);
		
		Vec3d[] hitVecs = new Vec3d[sides.length];
		for(int i = 0; i < sides.length; i++)
		{
			Vec3i dirVec = sides[i].getDirectionVec();
			Vec3d relHitVec = new Vec3d(relCenter.xCoord * dirVec.getX(),
				relCenter.yCoord * dirVec.getY(),
				relCenter.zCoord * dirVec.getZ());
			hitVecs[i] = center.add(relHitVec);
		}
		
		for(int i = 0; i < sides.length; i++)
		{
			// check line of sight
			if(WMinecraft.getWorld().rayTraceBlocks(eyesPos, hitVecs[i], false,
				true, false) != null)
				continue;
			
			side = sides[i];
			break;
		}
		
		if(side == null)
		{
			double distanceSqToCenter = eyesPos.squareDistanceTo(center);
			for(int i = 0; i < sides.length; i++)
			{
				// check if side is facing towards player
				if(eyesPos.squareDistanceTo(hitVecs[i]) >= distanceSqToCenter)
					continue;
				
				side = sides[i];
				break;
			}
		}
		
		if(side == null)
			throw new RuntimeException(
				"How could none of the sides be facing towards the player?!");
		
		// face block
		RotationUtils.faceVectorPacket(hitVecs[side.ordinal()]);
		
		// damage block
		if(!mc.playerController.onPlayerDamageBlock(pos, side))
			return false;
		
		// swing arm
		WPlayer.swingArmPacket();
		
		return true;
	}
	
	public static boolean breakBlockSimple_old(BlockPos pos)
	{
		Vec3d eyesPos = RotationUtils.getEyesPos();
		Vec3d posVec = new Vec3d(pos).addVector(0.5, 0.5, 0.5);
		double distanceSqPosVec = eyesPos.squareDistanceTo(posVec);
		
		for(EnumFacing side : EnumFacing.values())
		{
			Vec3d hitVec =
				posVec.add(new Vec3d(side.getDirectionVec()).scale(0.5));
			double distanceSqHitVec = eyesPos.squareDistanceTo(hitVec);
			
			// check if hitVec is within range (6 blocks)
			if(distanceSqHitVec > 36)
				continue;
			
			// check if side is facing towards player
			if(distanceSqHitVec >= distanceSqPosVec)
				continue;
			
			// AutoTool
			WurstClient.INSTANCE.mods.autoToolMod.setSlot(pos);
			
			// face block
			RotationUtils.faceVectorPacket(hitVec);
			
			// damage block
			if(!mc.playerController.onPlayerDamageBlock(pos, side))
				return false;
			
			// swing arm
			WPlayer.swingArmPacket();
			
			return true;
		}
		
		return false;
	}
	
	public static void breakBlockPacketSpam(BlockPos pos)
	{
		Vec3d eyesPos = RotationUtils.getEyesPos();
		Vec3d posVec = new Vec3d(pos).addVector(0.5, 0.5, 0.5);
		double distanceSqPosVec = eyesPos.squareDistanceTo(posVec);
		
		for(EnumFacing side : EnumFacing.values())
		{
			Vec3d hitVec =
				posVec.add(new Vec3d(side.getDirectionVec()).scale(0.5));
			
			// check if side is facing towards player
			if(eyesPos.squareDistanceTo(hitVec) >= distanceSqPosVec)
				continue;
			
			// break block
			WConnection.sendPacket(new CPacketPlayerDigging(
				Action.START_DESTROY_BLOCK, pos, side));
			WConnection.sendPacket(
				new CPacketPlayerDigging(Action.STOP_DESTROY_BLOCK, pos, side));
			
			return;
		}
	}
	
	public static void breakBlocksPacketSpam(Iterable<BlockPos> blocks)
	{
		Vec3d eyesPos = RotationUtils.getEyesPos();
		
		for(BlockPos pos : blocks)
		{
			Vec3d posVec = new Vec3d(pos).addVector(0.5, 0.5, 0.5);
			double distanceSqPosVec = eyesPos.squareDistanceTo(posVec);
			
			for(EnumFacing side : EnumFacing.values())
			{
				Vec3d hitVec =
					posVec.add(new Vec3d(side.getDirectionVec()).scale(0.5));
				
				// check if side is facing towards player
				if(eyesPos.squareDistanceTo(hitVec) >= distanceSqPosVec)
					continue;
				
				// break block
				WConnection.sendPacket(new CPacketPlayerDigging(
					Action.START_DESTROY_BLOCK, pos, side));
				WConnection.sendPacket(new CPacketPlayerDigging(
					Action.STOP_DESTROY_BLOCK, pos, side));
				
				break;
			}
		}
	}
	
	public static boolean rightClickBlockLegit(BlockPos pos)
	{
		Vec3d eyesPos = RotationUtils.getEyesPos();
		Vec3d posVec = new Vec3d(pos).addVector(0.5, 0.5, 0.5);
		double distanceSqPosVec = eyesPos.squareDistanceTo(posVec);
		
		for(EnumFacing side : EnumFacing.values())
		{
			Vec3d hitVec =
				posVec.add(new Vec3d(side.getDirectionVec()).scale(0.5));
			double distanceSqHitVec = eyesPos.squareDistanceTo(hitVec);
			
			// check if hitVec is within range (4.25 blocks)
			if(distanceSqHitVec > 18.0625)
				continue;
			
			// check if side is facing towards player
			if(distanceSqHitVec >= distanceSqPosVec)
				continue;
			
			// check line of sight
			if(WMinecraft.getWorld().rayTraceBlocks(eyesPos, hitVec, false,
				true, false) != null)
				continue;
			
			// face block
			if(!RotationUtils.faceVectorPacket(hitVec))
				return true;
			
			// place block
			WPlayerController.processRightClickBlock(pos, side, hitVec);
			WPlayer.swingArmClient();
			mc.rightClickDelayTimer = 4;
			
			return true;
		}
		
		return false;
	}
	
	public static boolean rightClickBlockSimple(BlockPos pos)
	{
		Vec3d eyesPos = RotationUtils.getEyesPos();
		Vec3d posVec = new Vec3d(pos).addVector(0.5, 0.5, 0.5);
		double distanceSqPosVec = eyesPos.squareDistanceTo(posVec);
		
		for(EnumFacing side : EnumFacing.values())
		{
			Vec3d hitVec =
				posVec.add(new Vec3d(side.getDirectionVec()).scale(0.5));
			double distanceSqHitVec = eyesPos.squareDistanceTo(hitVec);
			
			// check if hitVec is within range (6 blocks)
			if(distanceSqHitVec > 36)
				continue;
			
			// check if side is facing towards player
			if(distanceSqHitVec >= distanceSqPosVec)
				continue;
			
			// place block
			WPlayerController.processRightClickBlock(pos, side, hitVec);
			
			return true;
		}
		
		return false;
	}
	
	public static Iterable<BlockPos> getValidBlocksByDistance(double range,
		boolean ignoreVisibility, BlockValidator validator)
	{
		// prepare range check
		Vec3d eyesPos = RotationUtils.getEyesPos().subtract(0.5, 0.5, 0.5);
		double rangeSq = Math.pow(range + 0.5, 2);
		
		// set start pos
		BlockPos startPos = new BlockPos(RotationUtils.getEyesPos());
		
		return () -> new AbstractIterator<BlockPos>()
		{
			// initialize queue
			private ArrayDeque<BlockPos> queue =
				new ArrayDeque<>(Arrays.asList(startPos));
			private HashSet<BlockPos> visited = new HashSet<>();
			
			@Override
			protected BlockPos computeNext()
			{
				// find block using breadth first search
				while(!queue.isEmpty())
				{
					BlockPos current = queue.pop();
					
					// check range
					if(eyesPos.squareDistanceTo(new Vec3d(current)) > rangeSq)
						continue;
					
					boolean canBeClicked = WBlock.canBeClicked(current);
					
					if(ignoreVisibility || !canBeClicked)
						// add neighbors
						for(EnumFacing facing : EnumFacing.values())
						{
							BlockPos next = current.offset(facing);
							
							if(visited.contains(next))
								continue;
							
							queue.add(next);
							visited.add(next);
						}
					
					// check if block is valid
					if(canBeClicked && validator.isValid(current))
						return current;
				}
				
				return endOfData();
			}
		};
	}
	
	public static Iterable<BlockPos> getValidBlocksByDistanceReversed(
		double range, boolean ignoreVisibility, BlockValidator validator)
	{
		ArrayDeque<BlockPos> validBlocks = new ArrayDeque<>();
		
		BlockUtils.getValidBlocksByDistance(range, ignoreVisibility, validator)
			.forEach((p) -> validBlocks.push(p));
		
		return validBlocks;
	}
	
	public static Iterable<BlockPos> getValidBlocks(double range,
		BlockValidator validator)
	{
		// prepare range check
		Vec3d eyesPos = RotationUtils.getEyesPos().subtract(0.5, 0.5, 0.5);
		double rangeSq = Math.pow(range + 0.5, 2);
		
		return getValidBlocks((int)Math.ceil(range), (pos) -> {
			
			// check range
			if(eyesPos.squareDistanceTo(new Vec3d(pos)) > rangeSq)
				return false;
			
			// check if block is valid
			return validator.isValid(pos);
		});
	}
	
	public static Iterable<BlockPos> getValidBlocks(int blockRange,
		BlockValidator validator)
	{
		BlockPos playerPos = new BlockPos(RotationUtils.getEyesPos());
		
		BlockPos min = playerPos.add(-blockRange, -blockRange, -blockRange);
		BlockPos max = playerPos.add(blockRange, blockRange, blockRange);
		
		return () -> new AbstractIterator<BlockPos>()
		{
			private BlockPos last;
			
			private BlockPos computeNextUnchecked()
			{
				if(last == null)
				{
					last = min;
					return last;
				}
				
				int x = last.getX();
				int y = last.getY();
				int z = last.getZ();
				
				if(z < max.getZ())
					z++;
				else if(x < max.getX())
				{
					z = min.getZ();
					x++;
				}else if(y < max.getY())
				{
					z = min.getZ();
					x = min.getX();
					y++;
				}else
					return null;
				
				last = new BlockPos(x, y, z);
				return last;
			}
			
			@Override
			protected BlockPos computeNext()
			{
				BlockPos pos;
				while((pos = computeNextUnchecked()) != null)
				{
					// skip air blocks
					if(WBlock.getMaterial(pos) == Material.AIR)
						continue;
					
					// check if block is valid
					if(!validator.isValid(pos))
						continue;
					
					return pos;
				}
				
				return endOfData();
			}
		};
	}
	
	public static interface BlockValidator
	{
		public boolean isValid(BlockPos pos);
	}
}
