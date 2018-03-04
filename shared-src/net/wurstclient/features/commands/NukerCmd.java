/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.commands;

import net.minecraft.block.Block;
import net.wurstclient.features.Cmd;
import net.wurstclient.features.HelpPage;
import net.wurstclient.features.mods.blocks.NukerMod;
import net.wurstclient.utils.ChatUtils;
import net.wurstclient.utils.MiscUtils;

@HelpPage("Commands/nuker")
public final class NukerCmd extends Cmd
{
	public NukerCmd()
	{
		super("nuker", "Changes the settings of Nuker.",
			"mode (normal|id|flat|smash)", "id <block_id>",
			"name <block_name>");
	}
	
	@Override
	public void call(String[] args) throws CmdException
	{
		NukerMod nuker = wurst.mods.nukerMod;
		if(args.length != 2)
			throw new CmdSyntaxError();
		else if(args[0].toLowerCase().equals("mode"))
		{
			// search mode by name
			String[] modeNames = nuker.mode.getModes();
			String newModeName = args[1];
			int newMode = -1;
			for(int i = 0; i < modeNames.length; i++)
				if(newModeName.equals(modeNames[i].toLowerCase()))
					newMode = i;
				
			// syntax error if mode does not exist
			if(newMode == -1)
				throw new CmdSyntaxError("Invalid mode");
			
			if(newMode != nuker.mode.getSelected())
				nuker.mode.setSelected(newMode);
			
			ChatUtils.message("Nuker mode set to \"" + args[1] + "\".");
		}else if(args[0].equalsIgnoreCase("id") && MiscUtils.isInteger(args[1]))
		{
			if(nuker.mode.getSelected() != 1)
			{
				nuker.mode.setSelected(1);
				ChatUtils.message("Nuker mode set to \"" + args[0] + "\".");
			}
			
			nuker.setId(Integer.valueOf(args[1]));
			ChatUtils.message("Nuker ID set to \"" + args[1] + "\".");
		}else if(args[0].equalsIgnoreCase("name"))
		{
			if(nuker.mode.getSelected() != 1)
			{
				nuker.mode.setSelected(1);
				ChatUtils.message("Nuker mode set to \"" + args[0] + "\".");
			}
			
			int newId = Block.getIdFromBlock(Block.getBlockFromName(args[1]));
			if(newId == -1)
				throw new CmdError(
					"The block \"" + args[1] + "\" could not be found.");
			
			nuker.setId(newId);
			ChatUtils
				.message("Nuker ID set to " + newId + " (" + args[1] + ").");
		}else
			throw new CmdSyntaxError();
	}
}
