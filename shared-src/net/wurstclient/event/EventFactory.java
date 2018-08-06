/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.event;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.wurstclient.WurstClient;
import net.wurstclient.events.CameraTransformViewBobbingListener.CameraTransformViewBobbingEvent;
import net.wurstclient.events.GetAmbientOcclusionLightValueListener.GetAmbientOcclusionLightValueEvent;
import net.wurstclient.events.PacketInputListener.PacketInputEvent;
import net.wurstclient.events.PlayerMoveListener.PlayerMoveEvent;
import net.wurstclient.events.RenderBlockModelListener.RenderBlockModelEvent;
import net.wurstclient.events.RenderTileEntityListener.RenderTileEntityEvent;
import net.wurstclient.events.SetOpaqueCubeListener.SetOpaqueCubeEvent;
import net.wurstclient.events.ShouldSideBeRenderedListener.ShouldSideBeRenderedEvent;

public final class EventFactory
{
	public static boolean cameraTransformViewBobbing()
	{
		CameraTransformViewBobbingEvent event =
			new CameraTransformViewBobbingEvent();
		WurstClient.INSTANCE.events.fire(event);
		return !event.isCancelled();
	}
	
	public static void onPlayerMove(EntityPlayerSP player)
	{
		WurstClient.INSTANCE.events.fire(new PlayerMoveEvent(player));
	}
	
	public static boolean setOpaqueCube()
	{
		SetOpaqueCubeEvent event = new SetOpaqueCubeEvent();
		WurstClient.INSTANCE.events.fire(event);
		return !event.isCancelled();
	}
	
	public static float getAmbientOcclusionLightValue(float f,
		IBlockState state)
	{
		GetAmbientOcclusionLightValueEvent event =
			new GetAmbientOcclusionLightValueEvent(state, f);
		WurstClient.INSTANCE.events.fire(event);
		return event.getLightValue();
	}
	
	public static boolean shouldSideBeRendered(boolean b, IBlockState state)
	{
		ShouldSideBeRenderedEvent event =
			new ShouldSideBeRenderedEvent(state, b);
		WurstClient.INSTANCE.events.fire(event);
		return event.isRendered();
	}
	
	public static boolean renderBlockModel(IBlockState state)
	{
		RenderBlockModelEvent event = new RenderBlockModelEvent(state);
		WurstClient.INSTANCE.events.fire(event);
		return !event.isCancelled();
	}
	
	public static boolean renderTileEntity(TileEntity tileEntity)
	{
		RenderTileEntityEvent event = new RenderTileEntityEvent(tileEntity);
		WurstClient.INSTANCE.events.fire(event);
		return !event.isCancelled();
	}
	
	public static boolean onReceivePacket(Packet packet)
	{
		PacketInputEvent event = new PacketInputEvent(packet);
		WurstClient.INSTANCE.events.fire(event);
		return !event.isCancelled();
	}
}
