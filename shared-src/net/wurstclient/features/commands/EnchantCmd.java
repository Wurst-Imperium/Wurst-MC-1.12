/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.commands;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.wurstclient.compatibility.WEnchantments;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.ChatOutputEvent;
import net.wurstclient.features.Cmd;
import net.wurstclient.features.HelpPage;
import net.wurstclient.utils.ChatUtils;

@HelpPage("Commands/enchant")
public final class EnchantCmd extends Cmd
{
	public EnchantCmd()
	{
		super("enchant", "Enchants items with everything.", "[all]");
	}
	
	@Override
	public void execute(String[] args) throws CmdError
	{
		if(!WMinecraft.getPlayer().capabilities.isCreativeMode)
			error("Creative mode only.");
		if(args.length == 0)
		{
			ItemStack currentItem =
				WMinecraft.getPlayer().inventory.getCurrentItem();
			if(currentItem == null)
				error("There is no item in your hand.");
			for(Enchantment enchantment : Enchantment.REGISTRY)
				try
				{
					if(enchantment == WEnchantments.SILK_TOUCH)
						continue;
					currentItem.addEnchantment(enchantment, 127);
				}catch(Exception e)
				{
					
				}
		}else if(args[0].equals("all"))
		{
			int items = 0;
			for(int i = 0; i < 40; i++)
			{
				ItemStack currentItem =
					WMinecraft.getPlayer().inventory.getStackInSlot(i);
				if(currentItem == null)
					continue;
				items++;
				for(Enchantment enchantment : Enchantment.REGISTRY)
					try
					{
						if(enchantment == WEnchantments.SILK_TOUCH)
							continue;
						currentItem.addEnchantment(enchantment, 127);
					}catch(Exception e)
					{
						
					}
			}
			if(items == 1)
				ChatUtils.message("Enchanted 1 item.");
			else
				ChatUtils.message("Enchanted " + items + " items.");
		}else
			syntaxError();
	}
	
	@Override
	public String getPrimaryAction()
	{
		return "Enchant Current Item";
	}
	
	@Override
	public void doPrimaryAction()
	{
		wurst.commands.onSentMessage(new ChatOutputEvent(".enchant", true));
	}
}
