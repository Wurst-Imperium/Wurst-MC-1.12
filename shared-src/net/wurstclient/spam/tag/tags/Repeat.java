/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.spam.tag.tags;

import net.wurstclient.spam.exceptions.InvalidArgumentException;
import net.wurstclient.spam.exceptions.MissingArgumentException;
import net.wurstclient.spam.exceptions.SpamException;
import net.wurstclient.spam.tag.Tag;
import net.wurstclient.spam.tag.TagData;
import net.wurstclient.utils.MiscUtils;

public class Repeat extends Tag
{
	public Repeat()
	{
		super("repeat", "Repeats a chat message or a part of a chat message.",
			"<repeat number>",
			"Repeating a part of a message:\n"
				+ "Spam<repeat 2>, spam</repeat>!\n" + "\n"
				+ "Repeating a message:<repeat 3>\n" + "Spam!</repeat>");
	}
	
	@Override
	public String process(TagData tagData) throws SpamException
	{
		String processed = "";
		if(tagData.getTagArgs().length == 0)
			throw new MissingArgumentException(
				"The <repeat> tag requires at least one argument.",
				tagData.getTagLine(), this);
		if(!MiscUtils.isInteger(tagData.getTagArgs()[0]))
			throw new InvalidArgumentException(
				"Invalid number in <repeat> tag: \"" + tagData.getTagArgs()[0]
					+ "\"",
				tagData.getTagLine(), this);
		int count = Integer.parseInt(tagData.getTagArgs()[0]);
		for(int i = 0; i < count; i++)
			processed += tagData.getTagContent();
		return processed;
	}
}
