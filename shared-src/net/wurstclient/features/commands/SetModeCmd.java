/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.commands;

import java.util.Iterator;

import net.wurstclient.features.Cmd;
import net.wurstclient.features.Feature;
import net.wurstclient.features.HelpPage;
import net.wurstclient.settings.ModeSetting;
import net.wurstclient.settings.Setting;

@HelpPage("Commands/setmode")
public final class SetModeCmd extends Cmd
{
	public SetModeCmd()
	{
		super("setmode",
			"Changes a mode setting of a feature. Allows you to\n"
				+ "switch modes through keybinds.",
			"<feature> <mode_setting> (<mode>|next|prev)");
	}
	
	@Override
	public void call(String[] args) throws CmdException
	{
		if(args.length != 3)
			throw new CmdSyntaxError();
		
		// find feature
		Feature feature = null;
		String featureName = args[0];
		for(Iterator itr = wurst.navigator.iterator(); itr.hasNext();)
		{
			Feature item = (Feature)itr.next();
			if(featureName.equalsIgnoreCase(item.getName()))
			{
				feature = item;
				break;
			}
		}
		if(feature == null)
			throw new CmdError(
				"A feature named \"" + featureName + "\" could not be found.");
		
		// find setting
		Setting setting = null;
		String settingName = args[1].replace("_", " ");
		for(Setting featureSetting : feature.getSettings())
			if(featureSetting.getName().equalsIgnoreCase(settingName))
			{
				setting = featureSetting;
				break;
			}
		if(setting == null)
			throw new CmdError("A setting named \"" + settingName
				+ "\" could not be found in " + feature.getName() + ".");
		
		// check that setting is mode setting
		if(!(setting instanceof ModeSetting))
			throw new CmdError(feature.getName() + " " + setting.getName()
				+ " is not a mode setting.");
		ModeSetting modeSetting = (ModeSetting)setting;
		
		// set mode
		String modeName = args[2].replace("_", " ");
		if(modeName.equalsIgnoreCase("next"))
			modeSetting.nextMode();
		else if(modeName.equalsIgnoreCase("prev"))
			modeSetting.prevMode();
		else
		{
			// find mode
			int mode = modeSetting.indexOf(modeName);
			if(mode == -1)
				throw new CmdError(
					"A " + feature.getName() + " " + setting.getName()
						+ " named \"" + modeName + "\" could not be found.");
			
			// set mode
			modeSetting.setSelected(mode);
		}
	}
}
