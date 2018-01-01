/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.utils;

import java.util.Collections;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.wurstclient.features.mods.render.XRayMod;

public class XRayUtils
{
	public static void initXRayBlocks()
	{
		// ores
		add(Blocks.COAL_ORE);
		add(Blocks.COAL_BLOCK);
		add(Blocks.IRON_ORE);
		add(Blocks.IRON_BLOCK);
		add(Blocks.GOLD_ORE);
		add(Blocks.GOLD_BLOCK);
		add(Blocks.LAPIS_ORE);
		add(Blocks.LAPIS_BLOCK);
		add(Blocks.REDSTONE_ORE);
		add(Blocks.LIT_REDSTONE_ORE);
		add(Blocks.REDSTONE_BLOCK);
		add(Blocks.DIAMOND_ORE);
		add(Blocks.DIAMOND_BLOCK);
		add(Blocks.EMERALD_ORE);
		add(Blocks.EMERALD_BLOCK);
		add(Blocks.QUARTZ_ORE);
		
		// sort-of ores
		add(Blocks.CLAY);
		add(Blocks.BONE_BLOCK);
		add(Blocks.GLOWSTONE);
		
		// utilities
		add(Blocks.CRAFTING_TABLE);
		add(Blocks.FURNACE);
		add(Blocks.LIT_FURNACE);
		add(Blocks.TORCH);
		add(Blocks.LADDER);
		add(Blocks.TNT);
		add(Blocks.ENCHANTING_TABLE);
		add(Blocks.BOOKSHELF);
		add(Blocks.ANVIL);
		add(Blocks.BREWING_STAND);
		add(Blocks.BEACON);
		
		// storage
		add(Blocks.CHEST);
		add(Blocks.TRAPPED_CHEST);
		add(Blocks.ENDER_CHEST);
		add(Blocks.HOPPER);
		add(Blocks.DROPPER);
		add(Blocks.DISPENSER);
		
		// liquids
		add(Blocks.WATER);
		add(Blocks.FLOWING_WATER);
		add(Blocks.LAVA);
		add(Blocks.FLOWING_LAVA);
		
		// spawners
		add(Blocks.MOSSY_COBBLESTONE);
		add(Blocks.MOB_SPAWNER);
		
		// portals
		add(Blocks.PORTAL);
		add(Blocks.END_PORTAL);
		add(Blocks.END_PORTAL_FRAME);
		
		// command blocks
		add(Blocks.COMMAND_BLOCK);
		add(Blocks.CHAIN_COMMAND_BLOCK);
		add(Blocks.REPEATING_COMMAND_BLOCK);
	}
	
	private static void add(Block block)
	{
		XRayMod.xrayBlocks.add(block);
	}
	
	public static boolean isXRayBlock(Block blockToCheck)
	{
		return XRayMod.xrayBlocks.contains(blockToCheck);
	}
	
	public static void sortBlocks()
	{
		Collections.sort(XRayMod.xrayBlocks,
			(o1, o2) -> Block.getIdFromBlock(o1) - Block.getIdFromBlock(o2));
	}
}
