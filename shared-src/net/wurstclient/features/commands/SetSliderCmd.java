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
import net.wurstclient.settings.Setting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.utils.MiscUtils;

@HelpPage("Commands/setslider")
public final class SetSliderCmd extends Cmd
{
	public SetSliderCmd()
	{
		super("setslider",
			"Changes a slider setting of a feature. Allows you to\n"
				+ "move sliders through keybinds.",
			"<feature> <slider_setting> (<value>|more|less)");
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
		
		// check that setting is slider setting
		if(!(setting instanceof SliderSetting))
			throw new CmdError(feature.getName() + " " + setting.getName()
				+ " is not a slider setting.");
		SliderSetting sliderSetting = (SliderSetting)setting;
		
		// set value
		String valueName = args[2];
		if(valueName.equalsIgnoreCase("more"))
			sliderSetting.increaseValue();
		else if(valueName.equalsIgnoreCase("less"))
			sliderSetting.decreaseValue();
		else
		{
			// parse value
			if(!MiscUtils.isDouble(valueName))
				throw new CmdSyntaxError("Value must be a number.");
			double value = Double.parseDouble(valueName);
			
			// set value
			sliderSetting.setValue(value);
		}
	}
}
