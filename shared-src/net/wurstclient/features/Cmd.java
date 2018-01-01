/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features;

import java.util.ArrayList;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.keybinds.PossibleKeybind;
import net.wurstclient.utils.ChatUtils;
import net.wurstclient.utils.EntityUtils;
import net.wurstclient.utils.EntityUtils.TargetSettings;
import net.wurstclient.utils.MiscUtils;

public abstract class Cmd extends Feature
{
	private final String name;
	private final String description;
	private final String[] syntax;
	
	public Cmd(String name, String description, String... syntax)
	{
		this.name = name;
		this.description = description;
		this.syntax = syntax;
	}
	
	public final String getCmdName()
	{
		return name;
	}
	
	public final String[] getSyntax()
	{
		return syntax;
	}
	
	@Override
	public final String getName()
	{
		return "." + name;
	}
	
	@Override
	public final String getType()
	{
		return "Command";
	}
	
	@Override
	public final String getDescription()
	{
		String description = this.description;
		if(syntax.length > 0)
			description += "\n\nSyntax:";
		for(String element : syntax)
			description += "\n  ." + name + " " + element;
		return description;
	}
	
	@Override
	public final boolean isEnabled()
	{
		return false;
	}
	
	@Override
	public final boolean isBlocked()
	{
		return false;
	}
	
	@Override
	public final ArrayList<PossibleKeybind> getPossibleKeybinds()
	{
		return new ArrayList<>();
	}
	
	@Override
	public String getPrimaryAction()
	{
		return "";
	}
	
	@Override
	public void doPrimaryAction()
	{
		
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[0];
	}
	
	public final void printHelp()
	{
		for(String line : description.split("\n"))
			ChatUtils.message(line);
	}
	
	public final void printSyntax()
	{
		String output = "§o." + name + "§r";
		if(syntax.length != 0)
		{
			output += " " + syntax[0];
			for(int i = 1; i < syntax.length; i++)
				output += "\n    " + syntax[i];
		}
		for(String line : output.split("\n"))
			ChatUtils.message(line);
	}
	
	protected final BlockPos argsToPos(String... args) throws CmdException
	{
		if(args.length == 3)
		{
			BlockPos playerPos = new BlockPos(WMinecraft.getPlayer());
			int[] player =
				new int[]{playerPos.getX(), playerPos.getY(), playerPos.getZ()};
			int[] pos = new int[3];
			
			for(int i = 0; i < 3; i++)
				if(MiscUtils.isInteger(args[i]))
					pos[i] = Integer.parseInt(args[i]);
				else if(args[i].equals("~"))
					pos[i] = player[i];
				else if(args[i].startsWith("~")
					&& MiscUtils.isInteger(args[i].substring(1)))
					pos[i] = player[i] + Integer.parseInt(args[i].substring(1));
				else
					throw new CmdSyntaxError("Invalid coordinates.");
				
			return new BlockPos(pos[0], pos[1], pos[2]);
			
		}else if(args.length == 1)
		{
			TargetSettings settings = new TargetSettings()
			{
				@Override
				public boolean targetFriends()
				{
					return true;
				}
				
				@Override
				public boolean targetBehindWalls()
				{
					return true;
				}
			};
			
			Entity entity =
				EntityUtils.getClosestEntityWithName(args[0], settings);
			if(entity == null)
				throw new CmdError(
					"Entity \"" + args[0] + "\" could not be found.");
			return new BlockPos(entity);
			
		}else
			throw new CmdSyntaxError("Invalid coordinates.");
	}
	
	public abstract void call(String[] args) throws CmdException;
	
	public abstract class CmdException extends Exception
	{
		public CmdException()
		{
			super();
		}
		
		public CmdException(String message)
		{
			super(message);
		}
		
		public abstract void printToChat();
	}
	
	public final class CmdError extends CmdException
	{
		public CmdError(String message)
		{
			super(message);
		}
		
		@Override
		public void printToChat()
		{
			ChatUtils.error(getMessage());
		}
	}
	
	public final class CmdSyntaxError extends CmdException
	{
		public CmdSyntaxError()
		{
			super();
		}
		
		public CmdSyntaxError(String message)
		{
			super(message);
		}
		
		@Override
		public void printToChat()
		{
			if(getMessage() != null)
				ChatUtils.message("§4Syntax error:§r " + getMessage());
			
			printSyntax();
		}
	}
}
