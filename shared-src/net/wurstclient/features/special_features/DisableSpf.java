/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.special_features;

import net.wurstclient.features.SearchTags;
import net.wurstclient.features.Spf;

@SearchTags({"turn off", "hide wurst logo", "ghost mode", "stealth mode",
	"vanilla Minecraft"})
public final class DisableSpf extends Spf
{
	public DisableSpf()
	{
		super("Disable Wurst",
			"To disable Wurst, go to the Statistics screen and press the \"Disable Wurst\" button. It will turn\n"
				+ "into an \"Enable Wurst\" button once pressed.");
	}
}
