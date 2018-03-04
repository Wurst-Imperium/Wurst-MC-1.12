/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.commands;

import net.wurstclient.events.ChatOutputListener.ChatOutputEvent;
import net.wurstclient.features.Cmd;
import net.wurstclient.features.HelpPage;
import net.wurstclient.features.Mod;
import net.wurstclient.features.Spf;
import net.wurstclient.utils.ChatUtils;

@HelpPage("Commands/features")
public final class FeaturesCmd extends Cmd
{
	public FeaturesCmd()
	{
		super("features", "Shows the feature count and some over statistics.");
	}
	
	@Override
	public void call(String[] args) throws CmdException
	{
		if(args.length != 0)
			throw new CmdSyntaxError();
		
		ChatUtils
			.message("> All features: " + wurst.navigator.countAllFeatures());
		ChatUtils.message("> Mods: " + wurst.mods.countMods());
		ChatUtils.message("> Commands: " + wurst.commands.countCommands());
		ChatUtils
			.message("> Special features: " + wurst.special.countFeatures());
		int settings = 0, bypasses = 0;
		for(Mod mod : wurst.mods.getAllMods())
		{
			settings += mod.getSettings().size();
			if(mod.getClass().getAnnotation(Mod.Bypasses.class).mineplex())
				bypasses++;
		}
		ChatUtils.message("> NoCheat bypasses (mods only): " + bypasses);
		for(Cmd cmd : wurst.commands.getAllCommands())
			settings += cmd.getSettings().size();
		for(Spf spf : wurst.special.getAllFeatures())
			settings += spf.getSettings().size();
		ChatUtils.message("> Settings: " + settings);
	}
	
	@Override
	public String getPrimaryAction()
	{
		return "Show Statistics";
	}
	
	@Override
	public void doPrimaryAction()
	{
		wurst.commands.onSentMessage(new ChatOutputEvent(".features", true));
	}
}
