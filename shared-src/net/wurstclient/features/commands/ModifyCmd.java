/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
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
	public void call(String[] args) throws CmdException
	{
		EntityPlayerSP player = WMinecraft.getPlayer();
		
		if(!player.capabilities.isCreativeMode)
			throw new CmdError("Creative mode only.");
		
		if(args.length < 1)
			throw new CmdSyntaxError();
		
		ItemStack item = player.inventory.getCurrentItem();
		
		if(item == null)
			throw new CmdError("You need an item in your hand.");
		
		if(args[0].equalsIgnoreCase("add"))
		{
			if(args.length < 2)
				throw new CmdSyntaxError();
			
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
				throw new CmdError("NBT data is invalid.");
			}
		}else if(args[0].equalsIgnoreCase("set"))
		{
			if(args.length < 2)
				throw new CmdSyntaxError();
			
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
				throw new CmdError("NBT data is invalid.");
			}
		}else if(args[0].equalsIgnoreCase("remove"))
		{
			if(args.length != 2)
				throw new CmdSyntaxError();
			
			NBTPath path = parseNBTPath(item.getTagCompound(), args[1]);
			
			if(path == null)
				throw new CmdError("The path does not exist.");
			
			path.base.removeTag(path.key);
		}else if(args[0].equalsIgnoreCase("metadata"))
		{
			if(args.length != 2)
				throw new CmdSyntaxError();
			
			if(!MiscUtils.isInteger(args[1]))
				throw new CmdSyntaxError("Value must be a number.");
			
			item.setItemDamage(Integer.parseInt(args[1]));
		}else
			throw new CmdSyntaxError();
		
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
