/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.files;

import java.lang.reflect.Field;

import net.minecraft.crash.CrashReport;
import net.minecraft.util.ReportedException;
import net.wurstclient.keybinds.KeybindsConfig;

public final class ConfigFiles
{
	public static final OptionsConfig OPTIONS = new OptionsConfig();
	public static final ModsConfig MODS = new ModsConfig();
	public static final KeybindsConfig KEYBINDS = new KeybindsConfig();
	public static final NavigatorConfig NAVIGATOR = new NavigatorConfig();
	public static final AltsConfig ALTS = new AltsConfig();
	public static final FriendsConfig FRIENDS = new FriendsConfig();
	public static final XRayConfig XRAY = new XRayConfig();
	
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
