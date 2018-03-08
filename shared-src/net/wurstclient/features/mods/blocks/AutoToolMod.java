/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.blocks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.compatibility.WBlock;
import net.wurstclient.compatibility.WEnchantments;
import net.wurstclient.compatibility.WItem;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Feature;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;
import net.wurstclient.settings.CheckboxSetting;

@SearchTags({"auto tool"})
@Mod.Bypasses
public final class AutoToolMod extends Mod implements UpdateListener
{
	private final CheckboxSetting useSwords = new CheckboxSetting("Use swords",
		"Uses swords to break\n" + "leaves, cobwebs, etc.", false);
	
	private int oldSlot = -1;
	private BlockPos pos;
	private int timer;
	
	public AutoToolMod()
	{
		super("AutoTool",
			"Automatically equips the fastest\n"
				+ "applicable tool in your hotbar\n"
				+ "when you try to break a block.");
		setCategory(Category.BLOCKS);
		addSetting(useSwords);
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.mods.autoSwordMod, wurst.mods.nukerMod};
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
		
		// reset slot
		if(oldSlot != -1)
		{
			WMinecraft.getPlayer().inventory.currentItem = oldSlot;
			oldSlot = -1;
		}
	}
	
	@Override
	public void onUpdate()
	{
		// set slot if mining
		if(mc.gameSettings.keyBindAttack.pressed && mc.objectMouseOver != null
			&& mc.objectMouseOver.getBlockPos() != null)
			setSlot(mc.objectMouseOver.getBlockPos());
		
		// check if slot is set
		if(oldSlot == -1)
			return;
		
		// reset slot
		if(timer <= 0)
		{
			WMinecraft.getPlayer().inventory.currentItem = oldSlot;
			oldSlot = -1;
			return;
		}
		
		// update timer
		if(!mc.gameSettings.keyBindAttack.pressed
			|| WMinecraft.getPlayer().capabilities.isCreativeMode
			|| !WBlock.canBeClicked(pos))
			timer--;
	}
	
	public void setSlot(BlockPos pos)
	{
		// check if active
		if(!isActive())
			return;
		
		// check gamemode
		EntityPlayer player = WMinecraft.getPlayer();
		if(player.capabilities.isCreativeMode)
			return;
		
		// check if block can be clicked
		if(!WBlock.canBeClicked(pos))
			return;
		
		// initialize speed & slot
		IBlockState state = WBlock.getState(pos);
		float bestSpeed = getDestroySpeed(player.getHeldItemMainhand(), state);
		int bestSlot = -1;
		
		// find best tool
		for(int slot = 0; slot < 9; slot++)
		{
			if(slot == player.inventory.currentItem)
				continue;
			
			ItemStack stack = player.inventory.getStackInSlot(slot);
			
			float speed = getDestroySpeed(stack, state);
			if(speed <= bestSpeed)
				continue;
			
			if(!useSwords.isChecked() && stack.getItem() instanceof ItemSword)
				continue;
			
			bestSpeed = speed;
			bestSlot = slot;
		}
		
		// check if any tool was found
		if(bestSlot == -1)
			return;
		
		// save old slot
		if(oldSlot == -1)
			oldSlot = player.inventory.currentItem;
		
		// set slot
		player.inventory.currentItem = bestSlot;
		
		// save position
		this.pos = pos;
		
		// start timer
		timer = 4;
	}
	
	public void equipBestTool(BlockPos pos, boolean useSwords, boolean useHands)
	{
		EntityPlayer player = WMinecraft.getPlayer();
		if(player.capabilities.isCreativeMode)
			return;
		
		IBlockState state = WBlock.getState(pos);
		
		ItemStack heldItem = player.getHeldItemMainhand();
		float bestSpeed = getDestroySpeed(heldItem, state);
		int bestSlot = -1;
		
		boolean useFallback = useHands && isDamageable(heldItem);
		int fallbackSlot = -1;
		
		for(int slot = 0; slot < 9; slot++)
		{
			if(slot == player.inventory.currentItem)
				continue;
			
			ItemStack stack = player.inventory.getStackInSlot(slot);
			
			if(fallbackSlot == -1 && !isDamageable(stack))
				fallbackSlot = slot;
			
			float speed = getDestroySpeed(stack, state);
			if(speed <= bestSpeed)
				continue;
			
			if(!useSwords && stack.getItem() instanceof ItemSword)
				continue;
			
			bestSpeed = speed;
			bestSlot = slot;
		}
		
		if(bestSlot != -1)
			player.inventory.currentItem = bestSlot;
		else if(useFallback && bestSpeed <= 1 && fallbackSlot != -1)
			player.inventory.currentItem = fallbackSlot;
	}
	
	private float getDestroySpeed(ItemStack stack, IBlockState state)
	{
		float speed = WItem.getDestroySpeed(stack, state);
		
		if(speed > 1)
		{
			int efficiency = WEnchantments
				.getEnchantmentLevel(WEnchantments.EFFICIENCY, stack);
			if(efficiency > 0 && !WItem.isNullOrEmpty(stack))
				speed += efficiency * efficiency + 1;
		}
		
		return speed;
	}
	
	private boolean isDamageable(ItemStack stack)
	{
		return !WItem.isNullOrEmpty(stack) && stack.getItem().isDamageable();
	}
}
