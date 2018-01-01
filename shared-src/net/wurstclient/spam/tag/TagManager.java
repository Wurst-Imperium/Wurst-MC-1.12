/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.spam.tag;

import java.util.ArrayList;

import net.wurstclient.spam.exceptions.InvalidTagException;
import net.wurstclient.spam.exceptions.SpamException;
import net.wurstclient.spam.tag.tags.Random;
import net.wurstclient.spam.tag.tags.Repeat;
import net.wurstclient.spam.tag.tags.Var;

public class TagManager
{
	private final ArrayList<Tag> activeTags = new ArrayList<>();
	
	public Tag getTagByName(String name, int line) throws SpamException
	{
		for(int i = 0; i < activeTags.size(); i++)
			if(activeTags.get(i).getName().equals(name))
				return activeTags.get(i);
		throw new InvalidTagException(name, line);
	}
	
	public ArrayList<Tag> getActiveTags()
	{
		return activeTags;
	}
	
	public String process(TagData tagData) throws SpamException
	{
		Tag tag = getTagByName(tagData.getTagName(), tagData.getTagLine());
		String processedTag = tag.process(tagData);
		return processedTag;
	}
	
	public TagManager()
	{
		activeTags.add(new Random());
		activeTags.add(new Repeat());
		activeTags.add(new Var());
	}
}
