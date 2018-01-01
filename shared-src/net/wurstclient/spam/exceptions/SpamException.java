/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.spam.exceptions;

public class SpamException extends Exception
{
	public final int line;
	
	public SpamException(String message, int line)
	{
		super(message);
		this.line = line;
	}
	
	public String getHelp()
	{
		return "<html><center>Error! No help available.<br>Please report this at <a href=\"\">wurstclient.net/bugs</a>!";
	}
}
