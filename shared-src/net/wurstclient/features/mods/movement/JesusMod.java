/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.movement;

import net.minecraft.block.material.Material;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.WurstClient;
import net.wurstclient.compatibility.WBlock;
import net.wurstclient.compatibility.WConnection;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.PacketOutputListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;

@SearchTags({"WaterWalking", "water walking"})
@Mod.Bypasses(ghostMode = false)
public final class JesusMod extends Mod
	implements UpdateListener, PacketOutputListener
{
	private int tickTimer = 10;
	private int packetTimer = 0;
	
	public JesusMod()
	{
		super("Jesus", "Allows you to walk on water.\n"
			+ "The real Jesus used this hack ~2000 years ago.");
		setCategory(Category.MOVEMENT);
	}
	
	@Override
	public void onEnable()
	{
		WurstClient.INSTANCE.events.add(UpdateListener.class, this);
		WurstClient.INSTANCE.events.add(PacketOutputListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		WurstClient.INSTANCE.events.remove(UpdateListener.class, this);
		WurstClient.INSTANCE.events.remove(PacketOutputListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		// check if sneaking
		if(mc.gameSettings.keyBindSneak.pressed)
			return;
		
		// move up in water
		if(WMinecraft.getPlayer().isInWater())
		{
			WMinecraft.getPlayer().motionY = 0.11;
			tickTimer = 0;
			return;
		}
		
		// simulate jumping out of water
		if(tickTimer == 0)
			WMinecraft.getPlayer().motionY = 0.30;
		else if(tickTimer == 1)
			WMinecraft.getPlayer().motionY = 0;
		
		// update timer
		tickTimer++;
	}
	
	@Override
	public void onSentPacket(PacketOutputEvent event)
	{
		// check packet type
		if(!(event.getPacket() instanceof CPacketPlayer))
			return;
		
		CPacketPlayer packet = (CPacketPlayer)event.getPacket();
		
		// check if packet contains a position
		if(!(packet instanceof CPacketPlayer.Position
			|| packet instanceof CPacketPlayer.PositionRotation))
			return;
		
		// check inWater
		if(WMinecraft.getPlayer().isInWater())
			return;
		
		// check fall distance
		if(WMinecraft.getPlayer().fallDistance > 3F)
			return;
		
		if(!isOverLiquid())
			return;
		
		// if not actually moving, cancel packet
		if(WMinecraft.getPlayer().movementInput == null)
		{
			event.cancel();
			return;
		}
		
		// wait for timer
		packetTimer++;
		if(packetTimer < 4)
			return;
		
		// cancel old packet
		event.cancel();
		
		// get position
		double x = packet.getX(0);
		double y = packet.getY(0);
		double z = packet.getZ(0);
		
		// offset y
		if(WMinecraft.getPlayer().ticksExisted % 2 == 0)
			y -= 0.05;
		else
			y += 0.05;
		
		// create new packet
		Packet newPacket;
		if(packet instanceof CPacketPlayer.Position)
			newPacket = new CPacketPlayer.Position(x, y, z, true);
		else
			newPacket = new CPacketPlayer.PositionRotation(x, y, z,
				packet.getYaw(0), packet.getPitch(0), true);
		
		// send new packet
		WConnection.sendPacketBypass(newPacket);
	}
	
	public boolean isOverLiquid()
	{
		boolean foundLiquid = false;
		boolean foundSolid = false;
		
		// check collision boxes below player
		for(AxisAlignedBB bb : WMinecraft.getWorld().getCollisionBoxes(
			WMinecraft.getPlayer(),
			WMinecraft.getPlayer().boundingBox.offset(0, -0.5, 0)))
		{
			BlockPos pos = new BlockPos(bb.getCenter());
			Material material = WBlock.getMaterial(pos);
			
			if(material == Material.WATER || material == Material.LAVA)
				foundLiquid = true;
			else if(material != Material.AIR)
				foundSolid = true;
		}
		
		return foundLiquid && !foundSolid;
	}
	
	public boolean shouldBeSolid()
	{
		return isActive() && WMinecraft.getPlayer() != null
			&& WMinecraft.getPlayer().fallDistance <= 3
			&& !mc.gameSettings.keyBindSneak.pressed
			&& !WMinecraft.getPlayer().isInWater();
	}
}
