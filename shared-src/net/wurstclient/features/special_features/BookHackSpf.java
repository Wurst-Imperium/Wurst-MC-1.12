/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.special_features;

import net.wurstclient.features.Feature;
import net.wurstclient.features.HelpPage;
import net.wurstclient.features.SearchTags;
import net.wurstclient.features.Spf;

@SearchTags({"Force OP", "Book Hack", "OP Book", "command book"})
@HelpPage("Special_Features/Force_OP_(BookHack)")
public final class BookHackSpf extends Spf
{
	public BookHackSpf()
	{
		super("BookHack",
			"Allows you to insert links that execute commands into writable books. This can be used to\n"
				+ "trick other people (including admins) into executing commands like \"/op YourName\" or\n"
				+ "\"/kill\".");
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.mods.forceOpMod};
	}
}
