/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.settings;

import java.util.ArrayList;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import net.wurstclient.clickgui.Checkbox;
import net.wurstclient.clickgui.Component;
import net.wurstclient.files.ConfigFiles;
import net.wurstclient.keybinds.PossibleKeybind;
import net.wurstclient.navigator.NavigatorFeatureScreen;

public class CheckboxSetting extends Setting implements CheckboxLock
{
	private boolean checked;
	private final boolean checkedByDefault;
	private CheckboxLock lock;
	private int y;
	
	public CheckboxSetting(String name, String description, boolean checked)
	{
		super(name, description);
		this.checked = checked;
		checkedByDefault = checked;
	}
	
	public CheckboxSetting(String name, boolean checked)
	{
		this(name, null, checked);
	}
	
	@Override
	public final void addToFeatureScreen(NavigatorFeatureScreen featureScreen)
	{
		y = 60 + featureScreen.getTextHeight() + 4;
		
		featureScreen.addText("\n\n");
		featureScreen.addCheckbox(this);
	}
	
	@Override
	public final ArrayList<PossibleKeybind> getPossibleKeybinds(
		String featureName)
	{
		ArrayList<PossibleKeybind> possibleKeybinds = new ArrayList<>();
		String fullName = featureName + " " + getName();
		String command = ".setcheckbox " + featureName.toLowerCase() + " "
			+ getName().toLowerCase().replace(" ", "_") + " ";
		
		possibleKeybinds
			.add(new PossibleKeybind(command + "toggle", "Toggle " + fullName));
		possibleKeybinds
			.add(new PossibleKeybind(command + "on", "Enable " + fullName));
		possibleKeybinds
			.add(new PossibleKeybind(command + "off", "Disable " + fullName));
		
		return possibleKeybinds;
	}
	
	@Override
	public final boolean isChecked()
	{
		return isLocked() ? lock.isChecked() : checked;
	}
	
	public final boolean isCheckedByDefault()
	{
		return checkedByDefault;
	}
	
	public final void setChecked(boolean checked)
	{
		if(isLocked())
			return;
		
		this.checked = checked;
		update();
		ConfigFiles.SETTINGS.save();
	}
	
	public final void toggle()
	{
		setChecked(!isChecked());
	}
	
	public final void lock(CheckboxLock lock)
	{
		if(lock == this)
			throw new IllegalArgumentException(
				"Infinite loop of locks within locks");
		
		this.lock = lock;
		update();
	}
	
	public final void unlock()
	{
		lock = null;
		update();
	}
	
	public final boolean isLocked()
	{
		return lock != null;
	}
	
	public final int getY()
	{
		return y;
	}
	
	@Override
	public final Component getComponent()
	{
		return new Checkbox(this);
	}
	
	@Override
	public final void fromJson(JsonElement json)
	{
		if(!json.isJsonPrimitive())
			return;
		
		JsonPrimitive primitive = json.getAsJsonPrimitive();
		if(!primitive.isBoolean())
			return;
		
		checked = primitive.getAsBoolean();
		update();
	}
	
	@Override
	public final JsonElement toJson()
	{
		return new JsonPrimitive(checked);
	}
	
	@Override
	public final void legacyFromJson(JsonObject json)
	{
		try
		{
			checked = json.get(getName()).getAsBoolean();
		}catch(Exception e)
		{
			
		}
		
		update();
	}
}
