/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.combat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.util.DamageSource;
import net.wurstclient.compatibility.WEnchantments;
import net.wurstclient.compatibility.WEntityEquipmentSlot;
import net.wurstclient.compatibility.WItem;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.compatibility.WPlayerController;
import net.wurstclient.events.PacketOutputListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;

@SearchTags({"auto armor"})
@Mod.Bypasses
public final class AutoArmorMod extends Mod
	implements UpdateListener, PacketOutputListener
{
	private final CheckboxSetting useEnchantments = new CheckboxSetting(
		"Use enchantments", "Whether or not to consider the Protection\n"
			+ "enchantment when calculating armor strength.",
		true);
	private final CheckboxSetting swapWhileMoving =
		new CheckboxSetting("Swap while moving",
			"Whether or not to swap armor pieces\n"
				+ "while the player is moving.\n\n" + ChatFormatting.RED
				+ ChatFormatting.BOLD + "WARNING:" + ChatFormatting.RESET
				+ " This would not be possible\n"
				+ "without cheats. It may raise suspicion.",
			false);
	private final SliderSetting delay =
		new SliderSetting("Delay",
			"Amount of ticks to wait before swapping\n"
				+ "the next piece of armor.",
			2, 0, 20, 1, ValueDisplay.INTEGER);
	
	private int timer;
	
	public AutoArmorMod()
	{
		super("AutoArmor", "Manages your armor automatically.");
		setCategory(Category.COMBAT);
		addSetting(useEnchantments);
		addSetting(swapWhileMoving);
		addSetting(delay);
	}
	
	@Override
	public void onEnable()
	{
		timer = 0;
		wurst.events.add(UpdateListener.class, this);
		wurst.events.add(PacketOutputListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
		wurst.events.remove(PacketOutputListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		// wait for timer
		if(timer > 0)
		{
			timer--;
			return;
		}
		
		// check screen
		if(mc.currentScreen instanceof GuiContainer
			&& !(mc.currentScreen instanceof InventoryEffectRenderer))
			return;
		
		EntityPlayerSP player = WMinecraft.getPlayer();
		InventoryPlayer inventory = player.inventory;
		
		if(!swapWhileMoving.isChecked()
			&& (player.movementInput.moveForward != 0
				|| player.movementInput.moveStrafe != 0))
			return;
		
		// store slots and values of best armor pieces
		int[] bestArmorSlots = new int[4];
		int[] bestArmorValues = new int[4];
		
		// initialize with currently equipped armor
		for(int type = 0; type < 4; type++)
		{
			bestArmorSlots[type] = -1;
			
			ItemStack stack = inventory.armorItemInSlot(type);
			if(WItem.isNullOrEmpty(stack)
				|| !(stack.getItem() instanceof ItemArmor))
				continue;
			
			ItemArmor item = (ItemArmor)stack.getItem();
			bestArmorValues[type] = getArmorValue(item, stack);
		}
		
		// search inventory for better armor
		for(int slot = 0; slot < 36; slot++)
		{
			ItemStack stack = inventory.getStackInSlot(slot);
			
			if(WItem.isNullOrEmpty(stack)
				|| !(stack.getItem() instanceof ItemArmor))
				continue;
			
			ItemArmor item = (ItemArmor)stack.getItem();
			int armorType = WItem.getArmorType(item);
			int armorValue = getArmorValue(item, stack);
			
			if(armorValue > bestArmorValues[armorType])
			{
				bestArmorSlots[armorType] = slot;
				bestArmorValues[armorType] = armorValue;
			}
		}
		
		// equip better armor in random order
		ArrayList<Integer> types = new ArrayList<>(Arrays.asList(0, 1, 2, 3));
		Collections.shuffle(types);
		for(int type : types)
		{
			// check if better armor was found
			int slot = bestArmorSlots[type];
			if(slot == -1)
				continue;
				
			// check if armor can be swapped
			// needs 1 free slot where it can put the old armor
			ItemStack oldArmor = inventory.armorItemInSlot(type);
			if(!WItem.isNullOrEmpty(oldArmor)
				&& inventory.getFirstEmptyStack() == -1)
				continue;
			
			// hotbar fix
			if(slot < 9)
				slot += 36;
			
			// swap armor
			if(!WItem.isNullOrEmpty(oldArmor))
				WPlayerController.windowClick_QUICK_MOVE(8 - type);
			WPlayerController.windowClick_QUICK_MOVE(slot);
			
			break;
		}
	}
	
	@Override
	public void onSentPacket(PacketOutputEvent event)
	{
		if(event.getPacket() instanceof CPacketClickWindow)
			timer = delay.getValueI();
	}
	
	private int getArmorValue(ItemArmor item, ItemStack stack)
	{
		int armorPoints = item.damageReduceAmount;
		int prtPoints = 0;
		int armorToughness = (int)WItem.getArmorToughness(item);
		int armorType = item.getArmorMaterial()
			.getDamageReductionAmount(WEntityEquipmentSlot.LEGS);
		
		if(useEnchantments.isChecked())
		{
			Enchantment protection = WEnchantments.PROTECTION;
			int prtLvl = WEnchantments.getEnchantmentLevel(protection, stack);
			
			EntityPlayerSP player = WMinecraft.getPlayer();
			DamageSource dmgSource = DamageSource.causePlayerDamage(player);
			prtPoints = protection.calcModifierDamage(prtLvl, dmgSource);
		}
		
		return armorPoints * 5 + prtPoints * 3 + armorToughness + armorType;
	}
}
