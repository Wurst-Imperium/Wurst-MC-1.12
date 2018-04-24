/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.blocks;

import java.util.function.Supplier;

import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.compatibility.WBlock;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.LeftClickListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Feature;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;
import net.wurstclient.settings.EnumSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;
import net.wurstclient.utils.BlockUtils;
import net.wurstclient.utils.BlockUtils.BlockValidator;

@SearchTags({"FastNuker", "speed nuker", "fast nuker"})
@Mod.Bypasses(ghostMode = false,
	latestNCP = false,
	olderNCP = false,
	antiCheat = false,
	mineplex = false)
public final class SpeedNukerMod extends Mod
	implements LeftClickListener, UpdateListener
{
	private final SliderSetting range =
		new SliderSetting("Range", 5, 1, 6, 0.05, ValueDisplay.DECIMAL);
	private final EnumSetting<Mode> mode =
		new EnumSetting<>("Mode", Mode.values(), Mode.NORMAL);
	
	public SpeedNukerMod()
	{
		super("SpeedNuker",
			"Faster version of Nuker that cannot bypass NoCheat+.");
		setCategory(Category.BLOCKS);
	}
	
	@Override
	public void initSettings()
	{
		addSetting(range);
		addSetting(mode);
	}
	
	@Override
	public String getRenderName()
	{
		return mode.getSelected().renderName.get();
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.mods.nukerMod, wurst.mods.nukerLegitMod,
			wurst.mods.kaboomMod, wurst.mods.tunnellerMod};
	}
	
	@Override
	public void onEnable()
	{
		// disable other nukers
		wurst.mods.nukerMod.setEnabled(false);
		wurst.mods.nukerLegitMod.setEnabled(false);
		wurst.mods.tunnellerMod.setEnabled(false);
		
		// add listeners
		wurst.events.add(LeftClickListener.class, this);
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		// remove listeners
		wurst.events.remove(LeftClickListener.class, this);
		wurst.events.remove(UpdateListener.class, this);
		
		// resets
		wurst.mods.nukerMod.setId(0);
	}
	
	@Override
	public void onUpdate()
	{
		// abort if using IDNuker without an ID being set
		if(mode.getSelected() == Mode.ID && wurst.mods.nukerMod.getId() == 0)
			return;
		
		// get valid blocks
		Iterable<BlockPos> validBlocks =
			BlockUtils.getValidBlocksByDistanceReversed(range.getValue(), true,
				mode.getSelected().validator);
		
		// AutoTool
		for(BlockPos pos : validBlocks)
		{
			wurst.mods.autoToolMod.setSlot(pos);
			break;
		}
		
		// break all blocks
		validBlocks.forEach((pos) -> BlockUtils.breakBlockPacketSpam(pos));
	}
	
	@Override
	public void onLeftClick(LeftClickEvent event)
	{
		// check hitResult
		if(mc.objectMouseOver == null
			|| mc.objectMouseOver.getBlockPos() == null)
			return;
		
		// check mode
		if(mode.getSelected() != Mode.ID)
			return;
		
		// check material
		if(WBlock.getMaterial(mc.objectMouseOver.getBlockPos()) == Material.AIR)
			return;
		
		// set id
		wurst.mods.nukerMod
			.setId(WBlock.getId(mc.objectMouseOver.getBlockPos()));
	}
	
	private enum Mode
	{
		NORMAL("Normal", () -> "SpeedNuker", pos -> true),
		
		ID("ID", () -> "IDSpeedNuker [" + wurst.mods.nukerMod.getId() + "]",
			pos -> wurst.mods.nukerMod.getId() == WBlock.getId(pos)),
		
		FLAT("Flat", () -> "FlatSpeedNuker",
			pos -> pos.getY() >= WMinecraft.getPlayer().posY),
		
		SMASH("Smash", () -> "SmashSpeedNuker",
			pos -> WBlock.getHardness(pos) >= 1);
		
		private final String name;
		private final Supplier<String> renderName;
		private final BlockValidator validator;
		
		private Mode(String name, Supplier<String> renderName,
			BlockValidator validator)
		{
			this.name = name;
			this.renderName = renderName;
			this.validator = validator;
		}
		
		@Override
		public String toString()
		{
			return name;
		}
	}
}
