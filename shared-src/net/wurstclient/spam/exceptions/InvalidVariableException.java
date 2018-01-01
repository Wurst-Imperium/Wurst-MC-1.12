/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.spam.exceptions;

public class InvalidVariableException extends ExceptionWithDefaultHelp
{
	public InvalidVariableException(String varname, int line)
	{
		super("There is no variable called \"" + varname + "\".", line);
	}
}
