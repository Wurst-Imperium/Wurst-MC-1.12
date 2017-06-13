/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.commands;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.client.CPacketCreativeInventoryAction;
import net.wurstclient.compatibility.WConnection;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.features.Cmd;
import net.wurstclient.features.HelpPage;
import net.wurstclient.utils.ChatUtils;
import net.wurstclient.utils.MiscUtils;

@HelpPage("Commands/modify")
public final class ModifyCmd extends Cmd
{
	public ModifyCmd()
	{
		super("modify", "Modifies items in creative mode.", "add <nbt>",
			"remove <nbt_path>", "set <nbt>", "metadata <value>");
	}
	
	@Override
	public void execute(String[] args) throws CmdError
	{
		EntityPlayerSP player = WMinecraft.getPlayer();
		
		if(!player.capabilities.isCreativeMode)
			error("Creative mode only.");
		
		if(args.length < 1)
			syntaxError();
		
		ItemStack item = player.inventory.getCurrentItem();
		
		if(item == null)
			error("You need an item in your hand.");
		
		if(args[0].equalsIgnoreCase("add"))
		{
			if(args.length < 2)
				syntaxError();
			
			String v = "";
			for(int i = 1; i < args.length; i++)
				v += args[i] + " ";
			
			if(!item.hasTagCompound())
				item.setTagCompound(new NBTTagCompound());
			
			try
			{
				NBTTagCompound value = JsonToNBT.getTagFromJson(v);
				item.getTagCompound().merge(value);
			}catch(NBTException e)
			{
				e.printStackTrace();
				error("NBT data is invalid.");
			}
		}else if(args[0].equalsIgnoreCase("set"))
		{
			if(args.length < 2)
				syntaxError();
			
			String v = "";
			for(int i = 1; i < args.length; i++)
				v += args[i] + " ";
			
			try
			{
				NBTTagCompound value = JsonToNBT.getTagFromJson(v);
				item.setTagCompound(value);
			}catch(NBTException e)
			{
				e.printStackTrace();
				error("NBT data is invalid.");
			}
		}else if(args[0].equalsIgnoreCase("remove"))
		{
			if(args.length != 2)
				syntaxError();
			
			NBTPath path = parseNBTPath(item.getTagCompound(), args[1]);
			
			if(path == null)
				error("The path does not exist.");
			
			path.base.removeTag(path.key);
		}else if(args[0].equalsIgnoreCase("metadata"))
		{
			if(args.length != 2)
				syntaxError();
			
			if(!MiscUtils.isInteger(args[1]))
				syntaxError("Value must be a number.");
			
			item.setItemDamage(Integer.parseInt(args[1]));
		}else
			syntaxError();
		
		WConnection.sendPacket(new CPacketCreativeInventoryAction(
			36 + player.inventory.currentItem, item));
		
		ChatUtils.message("Item modified.");
	}
	
	private NBTPath parseNBTPath(NBTTagCompound tag, String path)
	{
		String[] parts = path.split("\\.");
		
		NBTTagCompound base = tag;
		if(base == null)
			return null;
		
		for(int i = 0; i < parts.length - 1; i++)
		{
			String part = parts[i];
			
			if(!base.hasKey(part)
				|| !(base.getTag(part) instanceof NBTTagCompound))
				return null;
			
			base = base.getCompoundTag(part);
		}
		
		if(!base.hasKey(parts[parts.length - 1]))
			return null;
		
		return new NBTPath(base, parts[parts.length - 1]);
	}
	
	private static class NBTPath
	{
		public NBTTagCompound base;
		public String key;
		
		public NBTPath(NBTTagCompound base, String key)
		{
			this.base = base;
			this.key = key;
		}
	}
}
