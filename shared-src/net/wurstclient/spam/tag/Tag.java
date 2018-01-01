/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.spam.tag;

import net.wurstclient.spam.SpamProcessor;
import net.wurstclient.spam.exceptions.SpamException;

public abstract class Tag
{
	private String name;
	private String description;
	private String syntax;
	private String example;
	
	public Tag(String name, String description, String syntax, String example)
	{
		this.name = name;
		this.description = description;
		this.syntax = syntax;
		this.example = example;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public String getSyntax()
	{
		return syntax;
	}
	
	public String getExample()
	{
		return example;
	}
	
	public String getHelp()
	{
		return "<html>" + "<table width=\"512px\"><tr><td>" + "<h1>&lt;" + name
			+ "&gt;</h1>" + "<h2>Description</h2>" + "<p>" + format(description)
			+ "</p>" + "<h2>Syntax</h2>"
			+ "<div bgcolor=\"#000000\" color=\"#00ff00\">" + "<code>"
			+ format(syntax) + "</code>" + "</div>" + "<h2>Example</h2>"
			+ "<div bgcolor=\"#000000\" color=\"#00ff00\">" + "<code>"
			+ format(example) + "</code>" + "</div><br>"
			+ "<p>Would be processed to:</p><br>"
			+ "<div bgcolor=\"#444444\" color=\"#ffffff\">" + "<p>"
			+ format(SpamProcessor.process(example, null, false)) + "</p>"
			+ "</div>" + "</td></tr></table>" + "</html>";
	}
	
	private String format(String string)
	{
		return string.replaceAll("<", "&lt;").replaceAll(">", "&gt;")
			.replaceAll("\n", "<br>");
	}
	
	public abstract String process(TagData tagData) throws SpamException;
}
