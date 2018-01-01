/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.commands;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.wurstclient.compatibility.WItem;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.compatibility.WPotion;
import net.wurstclient.features.Cmd;
import net.wurstclient.features.HelpPage;
import net.wurstclient.utils.MiscUtils;

@HelpPage("Commands/potion")
public final class PotionCmd extends Cmd
{
	public PotionCmd()
	{
		super("potion", "Changes the effects of the held potion.",
			"add (<effect> <amplifier> <duration>)...",
			"set (<effect> <amplifier> <duration>)...", "remove <effect>");
	}
	
	@Override
	public void call(String[] args) throws CmdException
	{
		if(args.length == 0)
			throw new CmdSyntaxError();
		if(!WMinecraft.getPlayer().capabilities.isCreativeMode)
			throw new CmdError("Creative mode only.");
		
		ItemStack currentItem =
			WMinecraft.getPlayer().inventory.getCurrentItem();
		if(!WItem.isPotion(currentItem))
			throw new CmdError("You are not holding a potion in your hand.");
		
		NBTTagList newEffects = new NBTTagList();
		
		// remove
		if(args[0].equalsIgnoreCase("remove"))
		{
			if(args.length != 2)
				throw new CmdSyntaxError();
			int id = 0;
			id = parsePotionEffectId(args[1]);
			List<PotionEffect> oldEffects =
				WPotion.getEffectsFromStack(currentItem);
			if(oldEffects != null)
				for(int i = 0; i < oldEffects.size(); i++)
				{
					PotionEffect temp = oldEffects.get(i);
					if(WPotion.getIdFromEffect(temp) != id)
					{
						NBTTagCompound effect = new NBTTagCompound();
						effect.setInteger("Id", WPotion.getIdFromEffect(temp));
						effect.setInteger("Amplifier", temp.getAmplifier());
						effect.setInteger("Duration", temp.getDuration());
						newEffects.appendTag(effect);
					}
				}
			currentItem.setTagInfo("CustomPotionEffects", newEffects);
			return;
		}else if((args.length - 1) % 3 != 0)
			throw new CmdSyntaxError();
		
		// add
		if(args[0].equalsIgnoreCase("add"))
		{
			List<PotionEffect> oldEffects =
				WPotion.getEffectsFromStack(currentItem);
			if(oldEffects != null)
				for(int i = 0; i < oldEffects.size(); i++)
				{
					PotionEffect temp = oldEffects.get(i);
					NBTTagCompound effect = new NBTTagCompound();
					effect.setInteger("Id", WPotion.getIdFromEffect(temp));
					effect.setInteger("Amplifier", temp.getAmplifier());
					effect.setInteger("Duration", temp.getDuration());
					newEffects.appendTag(effect);
				}
		}else if(!args[0].equalsIgnoreCase("set"))
			throw new CmdSyntaxError();
		
		// add & set
		for(int i = 0; i < (args.length - 1) / 3; i++)
		{
			int id = parsePotionEffectId(args[1 + i * 3]);
			int amplifier = 0;
			int duration = 0;
			
			if(MiscUtils.isInteger(args[2 + i * 3])
				&& MiscUtils.isInteger(args[3 + i * 3]))
			{
				amplifier = Integer.parseInt(args[2 + i * 3]) - 1;
				duration = Integer.parseInt(args[3 + i * 3]);
			}else
				throw new CmdSyntaxError();
			
			NBTTagCompound effect = new NBTTagCompound();
			effect.setInteger("Id", id);
			effect.setInteger("Amplifier", amplifier);
			effect.setInteger("Duration", duration * 20);
			newEffects.appendTag(effect);
		}
		System.out.println(newEffects);
		currentItem.setTagInfo("CustomPotionEffects", newEffects);
	}
	
	public int parsePotionEffectId(String input) throws CmdSyntaxError
	{
		int id = 0;
		try
		{
			id = Integer.parseInt(input);
		}catch(NumberFormatException var11)
		{
			try
			{
				id = WPotion.getIdFromResourceLocation(input);
			}catch(NullPointerException e)
			{
				throw new CmdSyntaxError();
			}
		}
		if(id < 1)
			throw new CmdSyntaxError();
		return id;
	}
}
