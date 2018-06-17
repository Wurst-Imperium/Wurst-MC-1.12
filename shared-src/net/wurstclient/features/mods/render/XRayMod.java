/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.render;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.init.Blocks;
import net.wurstclient.clickgui.EditBlockListScreen;
import net.wurstclient.compatibility.WBlock;
import net.wurstclient.events.GetAmbientOcclusionLightValueListener;
import net.wurstclient.events.RenderBlockModelListener;
import net.wurstclient.events.RenderTileEntityListener;
import net.wurstclient.events.SetOpaqueCubeListener;
import net.wurstclient.events.ShouldSideBeRenderedListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Feature;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;
import net.wurstclient.settings.BlockListSetting;

@SearchTags({"XRay", "x ray", "OreFinder", "ore finder"})
@Mod.Bypasses
public final class XRayMod extends Mod
	implements UpdateListener, SetOpaqueCubeListener,
	GetAmbientOcclusionLightValueListener, ShouldSideBeRenderedListener,
	RenderBlockModelListener, RenderTileEntityListener
{
	private final BlockListSetting blocks = new BlockListSetting("Blocks",
		Blocks.COAL_ORE, Blocks.COAL_BLOCK, Blocks.IRON_ORE, Blocks.IRON_BLOCK,
		Blocks.GOLD_ORE, Blocks.GOLD_BLOCK, Blocks.LAPIS_ORE,
		Blocks.LAPIS_BLOCK, Blocks.REDSTONE_ORE, Blocks.LIT_REDSTONE_ORE,
		Blocks.REDSTONE_BLOCK, Blocks.DIAMOND_ORE, Blocks.DIAMOND_BLOCK,
		Blocks.EMERALD_ORE, Blocks.EMERALD_BLOCK, Blocks.QUARTZ_ORE,
		Blocks.CLAY, Blocks.BONE_BLOCK, Blocks.GLOWSTONE, Blocks.CRAFTING_TABLE,
		Blocks.FURNACE, Blocks.LIT_FURNACE, Blocks.TORCH, Blocks.LADDER,
		Blocks.TNT, Blocks.ENCHANTING_TABLE, Blocks.BOOKSHELF, Blocks.ANVIL,
		Blocks.BREWING_STAND, Blocks.BEACON, Blocks.CHEST, Blocks.TRAPPED_CHEST,
		Blocks.ENDER_CHEST, Blocks.HOPPER, Blocks.DROPPER, Blocks.DISPENSER,
		Blocks.WATER, Blocks.FLOWING_WATER, Blocks.LAVA, Blocks.FLOWING_LAVA,
		Blocks.MOSSY_COBBLESTONE, Blocks.MOB_SPAWNER, Blocks.PORTAL,
		Blocks.END_PORTAL, Blocks.END_PORTAL_FRAME, Blocks.COMMAND_BLOCK,
		Blocks.CHAIN_COMMAND_BLOCK, Blocks.REPEATING_COMMAND_BLOCK);
	
	private ArrayList<String> blockNames;
	
	public XRayMod()
	{
		super("X-Ray", "Allows you to see ores through walls.");
		setCategory(Category.RENDER);
		addSetting(blocks);
	}
	
	@Override
	public String getRenderName()
	{
		return "X-Wurst";
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.commands.xRayCmd};
	}
	
	@Override
	public void onEnable()
	{
		blockNames = new ArrayList<>(blocks.getBlockNames());
		
		wurst.events.add(UpdateListener.class, this);
		wurst.events.add(SetOpaqueCubeListener.class, this);
		wurst.events.add(GetAmbientOcclusionLightValueListener.class, this);
		wurst.events.add(ShouldSideBeRenderedListener.class, this);
		wurst.events.add(RenderBlockModelListener.class, this);
		wurst.events.add(RenderTileEntityListener.class, this);
		mc.renderGlobal.loadRenderers();
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
		wurst.events.remove(SetOpaqueCubeListener.class, this);
		wurst.events.remove(GetAmbientOcclusionLightValueListener.class, this);
		wurst.events.remove(ShouldSideBeRenderedListener.class, this);
		wurst.events.remove(RenderBlockModelListener.class, this);
		wurst.events.remove(RenderTileEntityListener.class, this);
		mc.renderGlobal.loadRenderers();
		
		if(!wurst.mods.fullbrightMod.isActive())
			mc.gameSettings.gammaSetting = 0.5F;
	}
	
	@Override
	public void onUpdate()
	{
		mc.gameSettings.gammaSetting = 16;
	}
	
	@Override
	public void onSetOpaqueCube(SetOpaqueCubeEvent event)
	{
		event.cancel();
	}
	
	@Override
	public void onGetAmbientOcclusionLightValue(
		GetAmbientOcclusionLightValueEvent event)
	{
		event.setLightValue(1);
	}
	
	@Override
	public void onShouldSideBeRendered(ShouldSideBeRenderedEvent event)
	{
		event.setRendered(isVisible(event.getState().getBlock()));
	}
	
	@Override
	public void onRenderBlockModel(RenderBlockModelEvent event)
	{
		if(!isVisible(event.getState().getBlock()))
			event.cancel();
	}
	
	@Override
	public void onRenderTileEntity(RenderTileEntityEvent event)
	{
		if(!isVisible(event.getTileEntity().getBlockType()))
			event.cancel();
	}
	
	private boolean isVisible(Block block)
	{
		String name = WBlock.getName(block);
		int index = Collections.binarySearch(blockNames, name);
		return index >= 0;
	}
	
	public List<String> getBlockNames()
	{
		return blocks.getBlockNames();
	}
	
	public int getIndex(String blockName)
	{
		return Collections.binarySearch(blocks.getBlockNames(), blockName);
	}
	
	public void addBlock(Block block)
	{
		blocks.add(block);
	}
	
	public void removeBlock(int index)
	{
		blocks.remove(index);
	}
	
	public void resetBlocks()
	{
		blocks.resetToDefaults();
	}
	
	public void showBlockListEditor(GuiScreen prevScreen)
	{
		mc.displayGuiScreen(new EditBlockListScreen(prevScreen, blocks));
	}
}
