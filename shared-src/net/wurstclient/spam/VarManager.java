/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.spam;

import java.util.HashMap;

public class VarManager
{
	private final HashMap<String, String> spammerVars = new HashMap<>();
	private final HashMap<String, String> userVars = new HashMap<>();
	
	public VarManager()
	{
		spammerVars.put("lt", "<");
		spammerVars.put("gt", ">");
		spammerVars.put("sp", " ");
		spammerVars.put("br", "\n");
	}
	
	public HashMap<String, String> getSpammerVars()
	{
		return spammerVars;
	}
	
	public void clearUserVars()
	{
		userVars.clear();
	}
	
	public void addUserVar(String name, String value)
	{
		userVars.put(name, value);
	}
	
	public String getValueOfVar(String varName)
	{
		if(varName.startsWith("_"))
			return spammerVars.get(varName.substring(1));
		else
			return userVars.get(varName);
	}
}
