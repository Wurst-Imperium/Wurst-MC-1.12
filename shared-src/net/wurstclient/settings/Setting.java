/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.settings;

import java.util.ArrayList;

import com.google.gson.JsonObject;

import net.wurstclient.clickgui.Component;
import net.wurstclient.keybinds.PossibleKeybind;
import net.wurstclient.navigator.NavigatorFeatureScreen;

public interface Setting
{
	public String getName();
	
	public void addToFeatureScreen(NavigatorFeatureScreen featureScreen);
	
	public ArrayList<PossibleKeybind> getPossibleKeybinds(String featureName);
	
	public abstract Component getComponent();
	
	public void save(JsonObject json);
	
	public void load(JsonObject json);
	
	public void update();
}
