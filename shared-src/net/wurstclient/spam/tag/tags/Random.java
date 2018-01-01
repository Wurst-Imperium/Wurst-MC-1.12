/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.spam.tag.tags;

import net.minecraft.util.ChatAllowedCharacters;
import net.wurstclient.spam.exceptions.InvalidArgumentException;
import net.wurstclient.spam.exceptions.MissingArgumentException;
import net.wurstclient.spam.exceptions.SpamException;
import net.wurstclient.spam.tag.Tag;
import net.wurstclient.spam.tag.TagData;
import net.wurstclient.utils.MiscUtils;

public class Random extends Tag
{
	private static final java.util.Random random = new java.util.Random();
	
	public Random()
	{
		super("random", "Generates random strings, numbers and junk.",
			"<random \"number\"|\"string\"|\"junk\" length>",
			"Random number: <random number 3>\n"
				+ "Random string: <random string 5>\n"
				+ "Random junk: <random junk 8>");
	}
	
	@Override
	public String process(TagData tagData) throws SpamException
	{
		if(tagData.getTagArgs().length < 2)
			throw new MissingArgumentException(
				"The <random> tag requires at least two arguments.",
				tagData.getTagLine(), this);
		if(!tagData.getTagArgs()[0].equals("number")
			&& !tagData.getTagArgs()[0].equals("string")
			&& !tagData.getTagArgs()[0].equals("junk"))
			throw new InvalidArgumentException(
				"Invalid type in <random> tag: \"" + tagData.getTagArgs()[0]
					+ "\"",
				tagData.getTagLine(), this);
		if(!MiscUtils.isInteger(tagData.getTagArgs()[1]))
			throw new InvalidArgumentException(
				"Invalid number in <random> tag: \"" + tagData.getTagArgs()[1]
					+ "\"",
				tagData.getTagLine(), this);
		String result = "";
		if(tagData.getTagArgs()[0].equals("number"))
			for(int i = 0; i < Integer.valueOf(tagData.getTagArgs()[1]); i++)
				result += random.nextInt(10);
		else if(tagData.getTagArgs()[0].equals("string"))
		{
			String alphabet =
				"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
			for(int i = 0; i < Integer.valueOf(tagData.getTagArgs()[1]); i++)
			{
				char nextChar =
					alphabet.charAt(random.nextInt(alphabet.length()));
				result += new String(new char[]{nextChar});
			}
		}else if(tagData.getTagArgs()[0].equals("junk"))
			for(int i = 0; i < Integer.valueOf(tagData.getTagArgs()[1]);)
			{
				byte[] nextChar = new byte[1];
				random.nextBytes(nextChar);
				if(ChatAllowedCharacters.isAllowedCharacter((char)nextChar[0]))
				{
					String nextString = new String(nextChar)
						.replace("<", "§_lt;").replace("§", "");
					result += nextString;
					i++;
				}
			}
		return result + tagData.getTagContent();
	}
}
