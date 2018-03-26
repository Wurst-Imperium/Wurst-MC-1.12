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
import com.google.gson.JsonPrimitive;

import net.wurstclient.clickgui.ComboBox;
import net.wurstclient.clickgui.Component;
import net.wurstclient.files.ConfigFiles;
import net.wurstclient.keybinds.PossibleKeybind;
import net.wurstclient.navigator.NavigatorFeatureScreen;

public final class EnumSetting<T extends Enum> extends Setting
{
	private final T[] values;
	private T selected;
	private final T defaultSelected;
	
	public EnumSetting(String name, String description, T[] values, T selected)
	{
		super(name, description);
		this.values = Objects.requireNonNull(values);
		this.selected = Objects.requireNonNull(selected);
		defaultSelected = selected;
	}
	
	public EnumSetting(String name, T[] values, T selected)
	{
		this(name, null, values, selected);
	}
	
	public T[] getValues()
	{
		return values;
	}
	
	public T getSelected()
	{
		return selected;
	}
	
	public T getDefaultSelected()
	{
		return defaultSelected;
	}
	
	public void setSelected(T selected)
	{
		this.selected = Objects.requireNonNull(selected);
		ConfigFiles.SETTINGS.save();
	}
	
	public void setSelected(String selected)
	{
		for(T value : values)
		{
			if(!value.toString().equalsIgnoreCase(selected))
				continue;
			
			setSelected(value);
			break;
		}
	}
	
	@Override
	public Component getComponent()
	{
		return new ComboBox(this);
	}
	
	@Override
	public void fromJson(JsonElement json)
	{
		if(!json.isJsonPrimitive())
			return;
		
		JsonPrimitive primitive = json.getAsJsonPrimitive();
		if(!primitive.isString())
			return;
		
		setSelected(primitive.getAsString());
	}
	
	@Override
	public JsonElement toJson()
	{
		return new JsonPrimitive(selected.toString());
	}
	
	@Override
	public void addToFeatureScreen(NavigatorFeatureScreen featureScreen)
	{
		
	}
	
	@Override
	public ArrayList<PossibleKeybind> getPossibleKeybinds(String featureName)
	{
		return new ArrayList<>();
	}
}
