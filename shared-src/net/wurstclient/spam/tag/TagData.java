/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.spam.tag;

public class TagData
{
	private int tagStart;
	private int tagLength;
	private int tagLine;
	private String tagName;
	private String[] tagArgs;
	
	public boolean isTagClosed()
	{
		return tagClosed;
	}
	
	public void setTagClosed(boolean tagClosed)
	{
		this.tagClosed = tagClosed;
	}
	
	private boolean tagClosed;
	private String tag;
	private String tagContent;
	private int tagContentLength;
	private String spam;
	
	public TagData(int tagStart, int tagLength, int tagLine, String tagName,
		String[] tagArgs, boolean tagClosed, String tag, String tagContent,
		int tagContentLength, String spam)
	{
		this.tagStart = tagStart;
		this.tagLength = tagLength;
		this.tagLine = tagLine;
		this.tagName = tagName;
		this.tagArgs = tagArgs;
		this.tagClosed = tagClosed;
		this.tag = tag;
		this.tagContent = tagContent;
		this.tagContentLength = tagContentLength;
		this.spam = spam;
	}
	
	public int getTagStart()
	{
		return tagStart;
	}
	
	public void setTagStart(int tagStart)
	{
		this.tagStart = tagStart;
	}
	
	public int getTagLength()
	{
		return tagLength;
	}
	
	public void setTagLength(int tagLength)
	{
		this.tagLength = tagLength;
	}
	
	public int getTagLine()
	{
		return tagLine;
	}
	
	public void setTagLine(int tagLine)
	{
		this.tagLine = tagLine;
	}
	
	public String getTagName()
	{
		return tagName;
	}
	
	public void setTagName(String tagName)
	{
		this.tagName = tagName;
	}
	
	public String[] getTagArgs()
	{
		return tagArgs;
	}
	
	public void setTagArgs(String[] tagArgs)
	{
		this.tagArgs = tagArgs;
	}
	
	public String getTag()
	{
		return tag;
	}
	
	public void setTag(String tag)
	{
		this.tag = tag;
	}
	
	public String getTagContent()
	{
		return tagContent;
	}
	
	public void setTagContent(String tagContent)
	{
		this.tagContent = tagContent;
	}
	
	public int getTagContentLength()
	{
		return tagContentLength;
	}
	
	public void setTagContentLength(int tagContentLength)
	{
		this.tagContentLength = tagContentLength;
	}
	
	public String getSpam()
	{
		return spam;
	}
	
	public void setSpam(String spam)
	{
		this.spam = spam;
	}
}
