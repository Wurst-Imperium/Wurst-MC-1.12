/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features;

import java.util.ArrayList;

import net.wurstclient.keybinds.PossibleKeybind;
import net.wurstclient.settings.Setting;

public abstract class Spf extends Feature
{
	private final String name;
	private final String description;
	
	public Spf(String name, String description)
	{
		this.name = name;
		this.description = description;
	}
	
	@Override
	public final String getName()
	{
		return name;
	}
	
	@Override
	public final String getType()
	{
		return "Special Feature";
	}
	
	@Override
	public String getDescription()
	{
		return description;
	}
	
	@Override
	public boolean isEnabled()
	{
		return false;
	}
	
	@Override
	public boolean isBlocked()
	{
		return false;
	}
	
	@Override
	public ArrayList<PossibleKeybind> getPossibleKeybinds()
	{
		ArrayList<PossibleKeybind> possibleKeybinds = new ArrayList<>();
		
		// settings keybinds
		for(Setting setting : getSettings())
			possibleKeybinds.addAll(setting.getPossibleKeybinds(name));
		
		return possibleKeybinds;
	}
	
	@Override
	public String getPrimaryAction()
	{
		return "";
	}
	
	@Override
	public void doPrimaryAction()
	{
		
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[0];
	}
}
