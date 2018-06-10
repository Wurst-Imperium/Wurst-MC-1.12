/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.settings;

import java.util.ArrayList;
import java.util.Objects;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.wurstclient.clickgui.Component;
import net.wurstclient.keybinds.PossibleKeybind;
import net.wurstclient.navigator.NavigatorFeatureScreen;

public abstract class Setting
{
	private final String name;
	private final String description;
	
	public Setting(String name, String description)
	{
		this.name = Objects.requireNonNull(name);
		this.description = description;
	}
	
	public final String getName()
	{
		return name;
	}
	
	public final String getDescription()
	{
		return description;
	}
	
	public abstract Component getComponent();
	
	public abstract void fromJson(JsonElement json);
	
	public abstract JsonElement toJson();
	
	public void legacyFromJson(JsonObject json)
	{
		
	}
	
	public void update()
	{
		
	}
	
	public void addToFeatureScreen(NavigatorFeatureScreen featureScreen)
	{
		
	}
	
	public abstract ArrayList<PossibleKeybind> getPossibleKeybinds(
		String featureName);
}
