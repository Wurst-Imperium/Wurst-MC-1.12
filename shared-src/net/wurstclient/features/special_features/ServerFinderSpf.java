/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.special_features;

import net.wurstclient.features.HelpPage;
import net.wurstclient.features.SearchTags;
import net.wurstclient.features.Spf;

@SearchTags({"Server Finder"})
@HelpPage("Special_Features/Server_Finder")
public final class ServerFinderSpf extends Spf
{
	public ServerFinderSpf()
	{
		super("ServerFinder",
			"ServerFinder is a tool for finding easy-to-grief Minecraft servers quickly and with little effort.\n"
				+ "It usually finds around 75 - 200 servers.");
	}
}
