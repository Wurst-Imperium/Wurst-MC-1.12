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
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.Setting;

@HelpPage("Commands/setcheckbox")
public final class SetCheckboxCmd extends Cmd
{
	public SetCheckboxCmd()
	{
		super("setcheckbox",
			"Changes a checkbox setting of a feature. Allows you\n"
				+ "to toggle checkboxes through keybinds.",
			"<feature> <checkbox_setting> (on|off|toggle)");
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
		
		// check that setting is checkbox setting
		if(!(setting instanceof CheckboxSetting))
			throw new CmdError(feature.getName() + " " + setting.getName()
				+ " is not a checkbox setting.");
		CheckboxSetting checkboxSetting = (CheckboxSetting)setting;
		
		// set check
		String valueName = args[2];
		if(valueName.equalsIgnoreCase("on"))
			checkboxSetting.setChecked(true);
		else if(valueName.equalsIgnoreCase("off"))
			checkboxSetting.setChecked(false);
		else if(valueName.equalsIgnoreCase("toggle"))
			checkboxSetting.toggle();
		else
			throw new CmdSyntaxError();
	}
}
