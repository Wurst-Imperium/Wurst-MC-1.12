/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.compatibility.WBlock;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.LeftClickEvent;
import net.wurstclient.events.listeners.LeftClickListener;
import net.wurstclient.events.listeners.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Feature;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.ModeSetting;
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
	private BlockValidator validator;
	
	public CheckboxSetting useNuker =
		new CheckboxSetting("Use Nuker settings", true)
		{
			@Override
			public void update()
			{
				if(isChecked())
				{
					NukerMod nuker = wurst.mods.nukerMod;
					range.lock(nuker.range);
					mode.lock(nuker.mode.getSelected());
				}else
				{
					range.unlock();
					mode.unlock();
				}
			}
		};
	public final SliderSetting range =
		new SliderSetting("Range", 6, 1, 6, 0.05, ValueDisplay.DECIMAL);
	public final ModeSetting mode = new ModeSetting("Mode",
		new String[]{"Normal", "ID", "Flat", "Smash"}, 0)
	{
		@Override
		public void update()
		{
			switch(getSelected())
			{
				default:
				case 0:
				// normal mode
				validator = (pos) -> true;
				break;
				
				case 1:
				// id mode
				validator =
					(pos) -> wurst.mods.nukerMod.id == WBlock.getId(pos);
				break;
				
				case 2:
				// flat mode
				validator = (pos) -> pos.getY() >= WMinecraft.getPlayer().posY;
				break;
				
				case 3:
				// smash mode
				validator = (pos) -> WBlock.getHardness(pos) >= 1;
				break;
			}
		}
	};
	
	public SpeedNukerMod()
	{
		super("SpeedNuker",
			"Faster version of Nuker that cannot bypass NoCheat+.");
		setCategory(Category.BLOCKS);
	}
	
	@Override
	public void initSettings()
	{
		addSetting(useNuker);
		addSetting(range);
		addSetting(mode);
	}
	
	@Override
	public String getRenderName()
	{
		switch(mode.getSelected())
		{
			case 0:
			return "SpeedNuker";
			
			case 1:
			return "IDSpeedNuker [" + wurst.mods.nukerMod.id + "]";
			
			default:
			return mode.getSelectedMode() + "SpeedNuker";
		}
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
		wurst.mods.nukerMod.id = 0;
	}
	
	@Override
	public void onUpdate()
	{
		// abort if using IDNuker without an ID being set
		if(mode.getSelected() == 1 && wurst.mods.nukerMod.id == 0)
			return;
		
		// get valid blocks
		Iterable<BlockPos> validBlocks =
			BlockUtils.getValidBlocksByDistanceReversed(range.getValue(), true,
				validator);
		
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
		if(mode.getSelected() != 1)
			return;
		
		// check material
		if(WBlock.getMaterial(mc.objectMouseOver.getBlockPos()) == Material.AIR)
			return;
		
		// set id
		wurst.mods.nukerMod.id = WBlock.getId(mc.objectMouseOver.getBlockPos());
	}
}
