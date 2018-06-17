/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.commands;

import java.util.List;

import net.minecraft.block.Block;
import net.wurstclient.compatibility.WBlock;
import net.wurstclient.features.Cmd;
import net.wurstclient.features.Feature;
import net.wurstclient.features.HelpPage;
import net.wurstclient.features.SearchTags;
import net.wurstclient.features.mods.render.XRayMod;
import net.wurstclient.utils.ChatUtils;
import net.wurstclient.utils.MiscUtils;

@HelpPage("Commands/xray")
@SearchTags({"X-Ray", "x ray", "OreFinder", "ore finder"})
public final class XRayCmd extends Cmd
{
	public XRayCmd()
	{
		super("xray", "Manages X-Ray's block list.", "add <block_name_or_id>",
			"remove <block_name_or_id>", "list [<page>]", "reset");
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.mods.xRayMod};
	}
	
	@Override
	public void call(String[] args) throws CmdException
	{
		if(args.length == 0)
			throw new CmdSyntaxError();
		
		switch(args[0].toLowerCase())
		{
			case "add":
			add(args);
			break;
			
			case "remove":
			remove(args);
			break;
			
			case "list":
			list(args);
			break;
			
			case "reset":
			wurst.mods.xRayMod.resetBlocks();
			ChatUtils.message("Reset X-Ray's block list to defaults.");
			break;
			
			default:
			throw new CmdSyntaxError();
		}
	}
	
	private void add(String[] args) throws CmdException
	{
		if(args.length != 2)
			throw new CmdSyntaxError();
		
		Block block = Block.getBlockFromName(args[1]);
		if(block == null)
			throw new CmdSyntaxError("Unknown block: \"" + args[1] + "\".");
		
		XRayMod xray = wurst.mods.xRayMod;
		String name = WBlock.getName(block);
		if(xray.getIndex(name) > 0)
			throw new CmdError(
				"X-Ray's block list already contains " + name + ".");
		
		xray.addBlock(block);
		ChatUtils.message("Added " + name + " to X-Ray.");
		
		if(xray.isActive())
			ChatUtils
				.message("It will start to show up after you restart X-Ray.");
	}
	
	private void remove(String[] args) throws CmdException
	{
		if(args.length != 2)
			throw new CmdSyntaxError();
		
		Block block = Block.getBlockFromName(args[1]);
		if(block == null)
			throw new CmdSyntaxError("Unknown block: \"" + args[1] + "\".");
		
		XRayMod xray = wurst.mods.xRayMod;
		String name = WBlock.getName(block);
		int index = xray.getIndex(name);
		if(index < 0)
			throw new CmdError(
				"X-Ray's block list does not contain " + name + ".");
		
		xray.removeBlock(index);
		ChatUtils.message("Removed " + name + " from X-Ray.");
		
		if(xray.isActive())
			ChatUtils
				.message("It will no longer show up after you restart X-Ray.");
	}
	
	private void list(String[] args) throws CmdSyntaxError
	{
		if(args.length > 2)
			throw new CmdSyntaxError();
		
		int page;
		if(args.length < 2)
			page = 1;
		else if(MiscUtils.isInteger(args[1]))
			page = Integer.parseInt(args[1]);
		else
			throw new CmdSyntaxError("Not a number: " + args[1]);
		
		List<String> blocks = wurst.mods.xRayMod.getBlockNames();
		int pages = Math.max((int)Math.ceil(blocks.size() / 8.0), 1);
		if(page > pages || page < 1)
			throw new CmdSyntaxError("Invalid page: " + page);
		
		ChatUtils.message("Total: " + blocks.size()
			+ (blocks.size() == 1 ? " block" : " blocks"));
		ChatUtils
			.message("X-Ray blocks list (page " + page + "/" + pages + ")");
		
		for(int i = (page - 1) * 8; i < blocks.size() && i < page * 8; i++)
			ChatUtils.message(blocks.get(i));
	}
}
