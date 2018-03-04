/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.combat;

import net.minecraft.item.Item;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.LeftClickListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Feature;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;
import net.wurstclient.utils.InventoryUtils;

@SearchTags({"auto sword"})
@Mod.Bypasses(ghostMode = false,
	latestNCP = false,
	olderNCP = false,
	antiCheat = false)
public final class AutoSwordMod extends Mod
	implements LeftClickListener, UpdateListener
{
	private int oldSlot = -1;
	private int timer;
	
	public AutoSwordMod()
	{
		super("AutoSword",
			"Automatically uses the best weapon in your hotbar to attack entities.\n"
				+ "Tip: This works with Killaura.");
		setCategory(Category.COMBAT);
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.mods.autoToolMod, wurst.mods.killauraMod};
	}
	
	@Override
	public void onEnable()
	{
		wurst.events.add(LeftClickListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(LeftClickListener.class, this);
		
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
		// update timer
		if(timer > 0)
		{
			timer--;
			return;
		}
		
		// reset slot
		if(oldSlot != -1)
		{
			WMinecraft.getPlayer().inventory.currentItem = oldSlot;
			oldSlot = -1;
		}
		wurst.events.remove(UpdateListener.class, this);
	}
	
	@Override
	public void onLeftClick(LeftClickEvent event)
	{
		// check hitResult
		if(mc.objectMouseOver == null || mc.objectMouseOver.entityHit == null)
			return;
		
		setSlot();
	}
	
	public void setSlot()
	{
		// check if active
		if(!isActive())
			return;
		
		// wait for AutoEat
		if(wurst.mods.autoEatMod.isEating())
			return;
		
		// find best weapon
		float bestDamage = 0;
		int bestSlot = -1;
		for(int i = 0; i < 9; i++)
		{
			// skip empty slots
			if(InventoryUtils.isSlotEmpty(i))
				continue;
			
			Item item =
				WMinecraft.getPlayer().inventory.getStackInSlot(i).getItem();
			
			// get damage
			float damage = 0;
			if(item instanceof ItemSword)
				damage = ((ItemSword)item).attackDamage;
			else if(item instanceof ItemTool)
				damage = ((ItemTool)item).damageVsEntity;
			
			// compare with previous best weapon
			if(damage > bestDamage)
			{
				bestDamage = damage;
				bestSlot = i;
			}
		}
		
		// check if any weapon was found
		if(bestSlot == -1)
			return;
		
		// save old slot
		if(oldSlot == -1)
			oldSlot = WMinecraft.getPlayer().inventory.currentItem;
		
		// set slot
		WMinecraft.getPlayer().inventory.currentItem = bestSlot;
		
		// start timer
		timer = 4;
		wurst.events.add(UpdateListener.class, this);
	}
}
