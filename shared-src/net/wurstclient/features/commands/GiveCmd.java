/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.commands;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.util.ResourceLocation;
import net.wurstclient.compatibility.WItem;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.features.Cmd;
import net.wurstclient.features.HelpPage;
import net.wurstclient.utils.ChatUtils;
import net.wurstclient.utils.InventoryUtils;
import net.wurstclient.utils.MiscUtils;

@HelpPage("Commands/give")
public final class GiveCmd extends Cmd
{
	private ItemTemplate[] templates = new ItemTemplate[]{
		new ItemTemplate("Knockback Stick", Items.STICK,
			"{ench:[{id:19, lvl:12}], display:{Name:§6Knockback Stick},"
				+ "HideFlags:63}"),
		
		new ItemTemplate("One Hit Sword", Items.DIAMOND_SWORD,
			"{AttributeModifiers:[" + "{AttributeName:generic.attackDamage,"
				+ "Name:generic.attackDamage, Amount:2147483647,"
				+ "Operation:0, UUIDMost:246216, UUIDLeast:24636}"
				+ "], display:{Name:§6One Hitter}, Unbreakable:1,"
				+ "HideFlags:63}"),
		
		new ItemTemplate("Super Bow", Items.BOW,
			"{ench:[" + "{id:48, lvl:32767}, {id:49, lvl:5}, {id:50, lvl:1},"
				+ "{id:51, lvl:1}"
				+ "], display:{Name:§6Super Bow}, HideFlags:63}"),
		
		new ItemTemplate("Super Thorns Chestplate", Items.DIAMOND_CHESTPLATE,
			"{ench:[" + "{id:7, lvl:32767}," + "{id:0, lvl:32767}"
				+ "], AttributeModifiers:["
				+ "{AttributeName:generic.maxHealth, Name:generic.maxHealth,"
				+ "Amount:200, Operation:0, UUIDMost:43631, UUIDLeast:2641}"
				+ "], display:{Name:§6Super Thorns Chestplate}, HideFlags:63,"
				+ "Unbreakable:1}"),
		
		new ItemTemplate("Super Potion", Items.POTIONITEM,
			"{CustomPotionEffects: ["
				+ "{Id:11, Amplifier:127, Duration:2147483647},"
				+ "{Id:10, Amplifier:127, Duration:2147483647},"
				+ "{Id:23, Amplifier:127, Duration:2147483647},"
				+ "{Id:16, Amplifier:0, Duration:2147483647},"
				+ "{Id:8, Amplifier:3, Duration:2147483647},"
				+ "{Id:1, Amplifier:5, Duration:2147483647},"
				+ "{Id:5, Amplifier:127, Duration:2147483647}],"
				+ "display:{Name:§6Super Potion}, HideFlags:63}"),
		
		new ItemTemplate("Griefer Potion", Items.POTIONITEM,
			"{CustomPotionEffects:["
				+ "{Id:3, Amplifier:127, Duration:2147483647}"
				+ "], display:{Name:§6Griefer Potion}, HideFlags:63}")};
	
	public GiveCmd()
	{
		super("give",
			"Gives you an item with custom NBT data. Requires creative mode.",
			"(<item_name>|<item_id>) [<amount>] [<metadata>] [<nbt>]",
			"template <template_id> [<amount>]", "templates");
	}
	
	@Override
	public void call(String[] args) throws CmdException
	{
		// validate input
		if(args.length < 1)
			throw new CmdSyntaxError();
		if(!WMinecraft.getPlayer().capabilities.isCreativeMode)
			throw new CmdError("Creative mode only.");
		
		// list all templates
		if(args[0].equalsIgnoreCase("templates"))
		{
			ChatUtils.message("§cItem templates:");
			for(int i = 0; i < templates.length; i++)
			{
				ItemTemplate template = templates[i];
				ChatUtils.message("§c" + (i + 1) + "§c: §6" + template.name);
			}
			return;
		}
		
		Item item = null;
		int amount = 1;
		int metadata = 0;
		String nbt = null;
		
		// prepare item
		if(args[0].equalsIgnoreCase("template"))
		{
			// item from template
			
			if(args.length < 2 || args.length > 3)
				throw new CmdSyntaxError();
			if(!MiscUtils.isInteger(args[1]))
				throw new CmdSyntaxError("Template ID must be a number.");
			int id = Integer.valueOf(args[1]);
			if(id < 1 || id > templates.length)
				throw new CmdError("Template ID is out of range.");
			
			ItemTemplate template = templates[id - 1];
			item = template.item;
			nbt = template.tag;
			if(args.length == 3)
				amount = parseAmount(item, args[2]);
		}else
		{
			// custom item
			
			// id/name
			item = WItem.getFromRegistry(new ResourceLocation(args[0]));
			if(item == null && MiscUtils.isInteger(args[0]))
				item = Item.getItemById(Integer.parseInt(args[0]));
			if(item == null)
				throw new CmdError(
					"Item \"" + args[0] + "\" could not be found.");
			
			// amount
			if(args.length >= 2)
				amount = parseAmount(item, args[1]);
			
			// metadata
			if(args.length >= 3)
			{
				if(!MiscUtils.isInteger(args[2]))
					throw new CmdSyntaxError("Metadata must be a number.");
				
				metadata = Integer.valueOf(args[2]);
			}
			
			// nbt data
			if(args.length >= 4)
			{
				nbt = args[3];
				for(int i = 4; i < args.length; i++)
					nbt += " " + args[i];
			}
		}
		
		// generate item
		ItemStack stack = new ItemStack(item, amount, metadata);
		if(nbt != null)
			try
			{
				stack.setTagCompound(JsonToNBT.getTagFromJson(nbt));
			}catch(NBTException e)
			{
				throw new CmdSyntaxError("NBT data is invalid.");
			}
		
		// give item
		if(InventoryUtils.placeStackInHotbar(stack))
			ChatUtils.message("Item" + (amount > 1 ? "s" : "") + " created.");
		else
			throw new CmdError("Please clear a slot in your hotbar.");
	}
	
	private int parseAmount(Item item, String input) throws CmdException
	{
		if(!MiscUtils.isInteger(input))
			throw new CmdSyntaxError("Amount must be a number.");
		int amount = Integer.valueOf(input);
		if(amount < 1)
			throw new CmdError("Amount must be 1 or more.");
		if(amount > item.getItemStackLimit())
			throw new CmdError("Amount is larger than the maximum stack size.");
		return amount;
	}
	
	private static class ItemTemplate
	{
		public String name;
		public Item item;
		public String tag;
		
		public ItemTemplate(String name, Item item, String tag)
		{
			this.name = name;
			this.item = item;
			this.tag = tag;
		}
	}
	
}
