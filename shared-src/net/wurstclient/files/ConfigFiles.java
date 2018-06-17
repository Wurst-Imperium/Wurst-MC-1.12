/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.files;

import java.lang.reflect.Field;

import net.minecraft.crash.CrashReport;
import net.minecraft.util.ReportedException;

public final class ConfigFiles
{
	public static final OptionsConfig OPTIONS = new OptionsConfig();
	public static final ModsConfig MODS = new ModsConfig();
	public static final SettingsConfig SETTINGS = new SettingsConfig();
	public static final NavigatorConfig NAVIGATOR = new NavigatorConfig();
	public static final AltsConfig ALTS = new AltsConfig();
	public static final FriendsConfig FRIENDS = new FriendsConfig();
	
	public static void initialize()
	{
		try
		{
			for(Field field : ConfigFiles.class.getFields())
				((Config)field.get(null)).initialize();
			
		}catch(ReflectiveOperationException e)
		{
			throw new ReportedException(
				CrashReport.makeCrashReport(e, "Initializing config files"));
		}
	}
}
