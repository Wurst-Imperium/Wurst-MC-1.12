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

import net.wurstclient.clickgui.Component;
import net.wurstclient.clickgui.Slider;
import net.wurstclient.compatibility.WMath;
import net.wurstclient.files.ConfigFiles;
import net.wurstclient.keybinds.PossibleKeybind;
import net.wurstclient.navigator.NavigatorFeatureScreen;

public class SliderSetting extends Setting implements SliderLock
{
	private double value;
	private final double defaultValue;
	
	private final double minimum;
	private final double maximum;
	
	private double usableMin;
	private double usableMax;
	
	private final double increment;
	private final ValueDisplay display;
	
	private SliderLock lock;
	private boolean disabled;
	
	private int y;
	
	public SliderSetting(String name, String description, double value,
		double minimum, double maximum, double increment, ValueDisplay display)
	{
		super(name, description);
		this.value = value;
		defaultValue = value;
		
		this.minimum = minimum;
		this.maximum = maximum;
		
		usableMin = minimum;
		usableMax = maximum;
		
		this.increment = increment;
		this.display = display;
	}
	
	public SliderSetting(String name, double value, double minimum,
		double maximum, double increment, ValueDisplay display)
	{
		this(name, null, value, minimum, maximum, increment, display);
	}
	
	@Override
	public final void addToFeatureScreen(NavigatorFeatureScreen featureScreen)
	{
		featureScreen.addText("\n" + getName() + ":");
		y = 60 + featureScreen.getTextHeight();
		featureScreen.addText("\n");
		
		featureScreen.addSlider(this);
	}
	
	@Override
	public final ArrayList<PossibleKeybind> getPossibleKeybinds(
		String featureName)
	{
		ArrayList<PossibleKeybind> binds = new ArrayList<>();
		
		String fullName = featureName + " " + getName();
		String cmd = ".setslider " + featureName.toLowerCase() + " "
			+ getName().toLowerCase().replace(" ", "_") + " ";
		
		binds.add(new PossibleKeybind(cmd + "more", "Increase " + fullName));
		binds.add(new PossibleKeybind(cmd + "less", "Decrease " + fullName));
		
		return binds;
	}
	
	@Override
	public final double getValue()
	{
		return WMath.clamp(isLocked() ? lock.getValue() : value, usableMin,
			usableMax);
	}
	
	public final float getValueF()
	{
		return (float)getValue();
	}
	
	public final int getValueI()
	{
		return (int)getValue();
	}
	
	public final void setValue(double value)
	{
		if(disabled || isLocked())
			return;
		
		value = (int)(value / increment) * increment;
		value = WMath.clamp(value, usableMin, usableMax);
		this.value = value;
		
		update();
		ConfigFiles.SETTINGS.save();
	}
	
	public final void increaseValue()
	{
		setValue(getValue() + increment);
	}
	
	public final void decreaseValue()
	{
		setValue(getValue() - increment);
	}
	
	public final void lock(SliderLock lock)
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
	
	public final String getValueString()
	{
		return display.getValueString(getValue());
	}
	
	public final double getDefaultValue()
	{
		return defaultValue;
	}
	
	public final double getMinimum()
	{
		return minimum;
	}
	
	public final double getMaximum()
	{
		return maximum;
	}
	
	public final double getRange()
	{
		return maximum - minimum;
	}
	
	public final double getIncrement()
	{
		return increment;
	}
	
	public final double getUsableMin()
	{
		return usableMin;
	}
	
	public final void setUsableMin(double usableMin)
	{
		if(usableMin < minimum)
			throw new IllegalArgumentException("usableMin < minimum");
		
		this.usableMin = usableMin;
		update();
	}
	
	public final void resetUsableMin()
	{
		usableMin = minimum;
		update();
	}
	
	public final double getUsableMax()
	{
		return usableMax;
	}
	
	public final void setUsableMax(double usableMax)
	{
		if(usableMax > maximum)
			throw new IllegalArgumentException("usableMax > maximum");
		
		this.usableMax = usableMax;
		update();
	}
	
	public final void resetUsableMax()
	{
		usableMax = maximum;
		update();
	}
	
	public final boolean isLimited()
	{
		return usableMax != maximum || usableMin != minimum;
	}
	
	public final boolean isDisabled()
	{
		return disabled;
	}
	
	public final void setDisabled(boolean disabled)
	{
		this.disabled = disabled;
	}
	
	public final int getY()
	{
		return y;
	}
	
	public final float getPercentage()
	{
		return (float)((getValue() - minimum) / getRange());
	}
	
	@Override
	public final Component getComponent()
	{
		return new Slider(this);
	}
	
	@Override
	public final void fromJson(JsonElement json)
	{
		if(!json.isJsonPrimitive())
			return;
		
		JsonPrimitive primitive = json.getAsJsonPrimitive();
		if(!primitive.isNumber())
			return;
		
		double value = primitive.getAsDouble();
		if(value > maximum || value < minimum)
			return;
		
		value = (int)(value / increment) * increment;
		value = WMath.clamp(value, usableMin, usableMax);
		this.value = value;
		
		update();
	}
	
	@Override
	public final JsonElement toJson()
	{
		return new JsonPrimitive(Math.round(value * 1e6) / 1e6);
	}
	
	@Override
	public final void legacyFromJson(JsonObject json)
	{
		double newValue = value;
		
		try
		{
			newValue = json.get(getName()).getAsDouble();
		}catch(Exception e)
		{
			
		}
		
		if(newValue > maximum || newValue < minimum)
			return;
		
		if(disabled || isLocked())
			return;
		
		newValue = (int)(newValue / increment) * increment;
		newValue = WMath.clamp(newValue, usableMin, usableMax);
		value = newValue;
		
		update();
	}
	
	public static interface ValueDisplay
	{
		public static final ValueDisplay DECIMAL =
			(v) -> Math.round(v * 1e6) / 1e6 + "";
		public static final ValueDisplay INTEGER = (v) -> (int)v + "";
		public static final ValueDisplay PERCENTAGE =
			(v) -> (int)(Math.round(v * 1e8) / 1e6) + "%";
		public static final ValueDisplay DEGREES = (v) -> (int)v + "°";
		public static final ValueDisplay NONE = (v) -> "";
		
		public String getValueString(double value);
	}
}
