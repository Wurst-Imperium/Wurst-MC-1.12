/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.utils;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.wurstclient.compatibility.WConnection;
import net.wurstclient.compatibility.WMath;
import net.wurstclient.compatibility.WMinecraft;

public class RotationUtils
{
	private static boolean fakeRotation;
	private static float serverYaw;
	private static float serverPitch;
	
	public static Vec3d getEyesPos()
	{
		return new Vec3d(WMinecraft.getPlayer().posX,
			WMinecraft.getPlayer().posY + WMinecraft.getPlayer().getEyeHeight(),
			WMinecraft.getPlayer().posZ);
	}
	
	public static Vec3d getClientLookVec()
	{
		float f = WMath.cos(-WMinecraft.getPlayer().rotationYaw * 0.017453292F
			- (float)Math.PI);
		float f1 = WMath.sin(-WMinecraft.getPlayer().rotationYaw * 0.017453292F
			- (float)Math.PI);
		float f2 =
			-WMath.cos(-WMinecraft.getPlayer().rotationPitch * 0.017453292F);
		float f3 =
			WMath.sin(-WMinecraft.getPlayer().rotationPitch * 0.017453292F);
		return new Vec3d(f1 * f2, f3, f * f2);
	}
	
	public static Vec3d getServerLookVec()
	{
		float f = WMath.cos(-serverYaw * 0.017453292F - (float)Math.PI);
		float f1 = WMath.sin(-serverYaw * 0.017453292F - (float)Math.PI);
		float f2 = -WMath.cos(-serverPitch * 0.017453292F);
		float f3 = WMath.sin(-serverPitch * 0.017453292F);
		return new Vec3d(f1 * f2, f3, f * f2);
	}
	
	private static float[] getNeededRotations(Vec3d vec)
	{
		Vec3d eyesPos = getEyesPos();
		
		double diffX = vec.xCoord - eyesPos.xCoord;
		double diffY = vec.yCoord - eyesPos.yCoord;
		double diffZ = vec.zCoord - eyesPos.zCoord;
		
		double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
		
		float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
		float pitch = (float)-Math.toDegrees(Math.atan2(diffY, diffXZ));
		
		return new float[]{WMath.wrapDegrees(yaw), WMath.wrapDegrees(pitch)};
	}
	
	private static float[] getNeededRotations2(Vec3d vec)
	{
		Vec3d eyesPos = getEyesPos();
		
		double diffX = vec.xCoord - eyesPos.xCoord;
		double diffY = vec.yCoord - eyesPos.yCoord;
		double diffZ = vec.zCoord - eyesPos.zCoord;
		
		double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
		
		float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
		float pitch = (float)-Math.toDegrees(Math.atan2(diffY, diffXZ));
		
		return new float[]{
			WMinecraft.getPlayer().rotationYaw
				+ WMath.wrapDegrees(yaw - WMinecraft.getPlayer().rotationYaw),
			WMinecraft.getPlayer().rotationPitch + WMath
				.wrapDegrees(pitch - WMinecraft.getPlayer().rotationPitch)};
	}
	
	public static double getAngleToLastReportedLookVec(Vec3d vec)
	{
		float[] needed = getNeededRotations(vec);
		
		EntityPlayerSP player = WMinecraft.getPlayer();
		float lastReportedYaw = WMath.wrapDegrees(player.lastReportedYaw);
		float lastReportedPitch = WMath.wrapDegrees(player.lastReportedPitch);
		
		float diffYaw = lastReportedYaw - needed[0];
		float diffPitch = lastReportedPitch - needed[1];
		
		return Math.sqrt(diffYaw * diffYaw + diffPitch * diffPitch);
	}
	
	public static float limitAngleChange(float current, float intended,
		float maxChange)
	{
		float change = WMath.wrapDegrees(intended - current);
		
		change = WMath.clamp(change, -maxChange, maxChange);
		
		return WMath.wrapDegrees(current + change);
	}
	
	public static boolean faceVectorPacket(Vec3d vec)
	{
		// use fake rotation in next packet
		fakeRotation = true;
		
		float[] rotations = getNeededRotations(vec);
		
		serverYaw = limitAngleChange(serverYaw, rotations[0], 30);
		serverPitch = rotations[1];
		
		return Math.abs(serverYaw - rotations[0]) < 1F;
	}
	
	public static void faceVectorPacketInstant(Vec3d vec)
	{
		float[] rotations = getNeededRotations2(vec);
		
		WConnection.sendPacket(new CPacketPlayer.Rotation(rotations[0],
			rotations[1], WMinecraft.getPlayer().onGround));
	}
	
	public static boolean faceVectorClient(Vec3d vec)
	{
		float[] rotations = getNeededRotations(vec);
		
		float oldYaw = WMinecraft.getPlayer().prevRotationYaw;
		float oldPitch = WMinecraft.getPlayer().prevRotationPitch;
		
		WMinecraft.getPlayer().rotationYaw =
			limitAngleChange(oldYaw, rotations[0], 30);
		WMinecraft.getPlayer().rotationPitch = rotations[1];
		
		return Math.abs(oldYaw - rotations[0])
			+ Math.abs(oldPitch - rotations[1]) < 1F;
	}
	
	public static boolean faceEntityClient(Entity entity)
	{
		// get position & rotation
		Vec3d eyesPos = getEyesPos();
		Vec3d lookVec = getServerLookVec();
		
		// try to face center of boundingBox
		AxisAlignedBB bb = entity.boundingBox;
		if(faceVectorClient(bb.getCenter()))
			return true;
		
		// if not facing center, check if facing anything in boundingBox
		return bb.calculateIntercept(eyesPos,
			eyesPos.add(lookVec.scale(6))) != null;
	}
	
	public static boolean faceEntityPacket(Entity entity)
	{
		// get position & rotation
		Vec3d eyesPos = getEyesPos();
		Vec3d lookVec = getServerLookVec();
		
		// try to face center of boundingBox
		AxisAlignedBB bb = entity.boundingBox;
		if(faceVectorPacket(bb.getCenter()))
			return true;
		
		// if not facing center, check if facing anything in boundingBox
		return bb.calculateIntercept(eyesPos,
			eyesPos.add(lookVec.scale(6))) != null;
	}
	
	public static boolean faceVectorForWalking(Vec3d vec)
	{
		float[] rotations = getNeededRotations(vec);
		
		float oldYaw = WMinecraft.getPlayer().prevRotationYaw;
		
		WMinecraft.getPlayer().rotationYaw =
			limitAngleChange(oldYaw, rotations[0], 30);
		WMinecraft.getPlayer().rotationPitch = 0;
		
		return Math.abs(oldYaw - rotations[0]) < 1F;
	}
	
	public static float getAngleToClientRotation(Vec3d vec)
	{
		float[] needed = getNeededRotations(vec);
		
		float diffYaw =
			WMath.wrapDegrees(WMinecraft.getPlayer().rotationYaw) - needed[0];
		float diffPitch =
			WMath.wrapDegrees(WMinecraft.getPlayer().rotationPitch) - needed[1];
		
		float angle =
			(float)Math.sqrt(diffYaw * diffYaw + diffPitch * diffPitch);
		
		return angle;
	}
	
	public static float getHorizontalAngleToClientRotation(Vec3d vec)
	{
		float[] needed = getNeededRotations(vec);
		
		float angle =
			WMath.wrapDegrees(WMinecraft.getPlayer().rotationYaw) - needed[0];
		
		return angle;
	}
	
	public static float getAngleToServerRotation(Vec3d vec)
	{
		float[] needed = getNeededRotations(vec);
		
		float diffYaw = serverYaw - needed[0];
		float diffPitch = serverPitch - needed[1];
		
		float angle =
			(float)Math.sqrt(diffYaw * diffYaw + diffPitch * diffPitch);
		
		return angle;
	}
	
	public static void updateServerRotation()
	{
		// disable fake rotation in next packet unless manually enabled again
		if(fakeRotation)
		{
			fakeRotation = false;
			return;
		}
		
		// slowly synchronize server rotation with client
		serverYaw =
			limitAngleChange(serverYaw, WMinecraft.getPlayer().rotationYaw, 30);
		serverPitch = WMinecraft.getPlayer().rotationPitch;
	}
	
	public static float getServerYaw()
	{
		return serverYaw;
	}
	
	public static float getServerPitch()
	{
		return serverPitch;
	}
}
