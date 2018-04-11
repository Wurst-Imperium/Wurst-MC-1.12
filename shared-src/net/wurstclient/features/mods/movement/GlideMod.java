/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.movement;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import net.minecraft.block.BlockLiquid;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.wurstclient.compatibility.WBlock;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.compatibility.WWorld;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Mod;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;

@Mod.Bypasses(ghostMode = false, latestNCP = false, olderNCP = false)
public final class GlideMod extends Mod implements UpdateListener
{
	private final SliderSetting fallSpeed = new SliderSetting("Fall speed",
		0.125, 0.005, 0.25, 0.005, ValueDisplay.DECIMAL);
	private final SliderSetting moveSpeed =
		new SliderSetting("Move speed", "Horizontal movement factor.", 1.2, 1,
			5, 0.05, ValueDisplay.PERCENTAGE);
	private final SliderSetting minHeight = new SliderSetting("Min height",
		"Won't glide when you are\n" + "too close to the ground.", 0, 0, 2,
		0.01,
		v -> v == 0 ? "disabled" : ValueDisplay.DECIMAL.getValueString(v));
	
	public GlideMod()
	{
		super("Glide", "Makes you glide down slowly when falling.");
		setCategory(Category.MOVEMENT);
		addSetting(fallSpeed);
		addSetting(moveSpeed);
		addSetting(minHeight);
	}
	
	@Override
	public void onEnable()
	{
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		EntityPlayerSP player = WMinecraft.getPlayer();
		
		if(!player.isAirBorne || player.isInWater() || player.isInLava()
			|| player.isOnLadder() || player.motionY >= 0)
			return;
		
		if(minHeight.getValue() > 0)
		{
			AxisAlignedBB box = player.getEntityBoundingBox();
			box = box.union(box.offset(0, -minHeight.getValue(), 0));
			if(WWorld.collidesWithAnyBlock(box))
				return;
			
			BlockPos min =
				new BlockPos(new Vec3d(box.minX, box.minY, box.minZ));
			BlockPos max =
				new BlockPos(new Vec3d(box.maxX, box.maxY, box.maxZ));
			Stream<BlockPos> stream = StreamSupport
				.stream(BlockPos.getAllInBox(min, max).spliterator(), true);
			
			// manual collision check, since liquids don't have bounding boxes
			if(stream.map(WBlock::getBlock)
				.anyMatch(b -> b instanceof BlockLiquid))
				return;
		}
		
		player.motionY = Math.max(player.motionY, -fallSpeed.getValue());
		player.jumpMovementFactor *= moveSpeed.getValueF();
	}
}
