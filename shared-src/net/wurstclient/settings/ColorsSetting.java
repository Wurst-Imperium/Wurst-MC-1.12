/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.settings;

import java.awt.Color;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import net.wurstclient.clickgui.Component;
import net.wurstclient.keybinds.PossibleKeybind;
import net.wurstclient.navigator.NavigatorFeatureScreen;
import net.wurstclient.utils.JsonUtils;

public class ColorsSetting extends Setting implements ColorsLock
{
	private boolean[] selected;
	private ColorsLock lock;
	
	public ColorsSetting(String name, boolean[] selected)
	{
		super(name, null);
		if(selected.length != 16)
			throw new IllegalArgumentException(
				"Length of 'selected' must be 16 but was " + selected.length
					+ " instead.");
		
		this.selected = selected;
	}
	
	@Override
	public final void addToFeatureScreen(NavigatorFeatureScreen featureScreen)
	{
		// text
		featureScreen.addText("\n" + getName() + ":\n\n\n\n\n\n\n");
		
		// color buttons
		class ColorButton extends NavigatorFeatureScreen.ButtonData
		{
			public int index;
			
			public ColorButton(NavigatorFeatureScreen featureScreen, int x,
				int y, String displayString, int color, int index)
			{
				featureScreen.super(x, y, 12, 12, displayString, color);
				
				this.index = index;
				textColor = color;
				
				updateColor();
			}
			
			@Override
			public void press()
			{
				setSelected(index, !selected[index]);
				updateColor();
			}
			
			@Override
			public boolean isLocked()
			{
				return ColorsSetting.this.isLocked();
			}
			
			public void updateColor()
			{
				color = new Color(selected[index] ? 0xcccccc : 0x222222);
			}
		}
		
		// add color buttons
		int x = featureScreen.getMiddleX() - 104;
		int y = 60 + featureScreen.getTextHeight() - 72;
		String[] colorNames = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
			"A", "B", "C", "D", "E", "F"};
		int[] colors = new int[]{0x000000, 0x0066cc, 0x00cc00, 0x00cccc,
			0xcc0000, 0xcc00cc, 0xff8800, 0xaaaaaa, 0x666666, 0x0000ff,
			0x00ff00, 0x00ffff, 0xff0000, 0xff8888, 0xffff00, 0xffffff};
		ColorButton[] buttons = new ColorButton[selected.length];
		for(int i = 0; i < selected.length; i++)
		{
			switch(i % 4)
			{
				case 0:
				x -= 48;
				y += 16;
				break;
				default:
				x += 16;
				break;
			}
			ColorButton button = new ColorButton(featureScreen, x, y,
				colorNames[i], colors[i], i);
			buttons[i] = button;
			featureScreen.addButton(button);
		}
		
		// all on button
		x += 16;
		y -= 48;
		featureScreen.addButton(
			featureScreen.new ButtonData(x, y, 48, 12, "All On", 0x404040)
			{
				@Override
				public void press()
				{
					for(int i = 0; i < buttons.length; i++)
					{
						selected[i] = true;
						buttons[i].updateColor();
					}
					update();
				}
				
				@Override
				public boolean isLocked()
				{
					return ColorsSetting.this.isLocked();
				}
			});
		
		// all off button
		y += 16;
		featureScreen.addButton(
			featureScreen.new ButtonData(x, y, 48, 12, "All Off", 0x404040)
			{
				@Override
				public void press()
				{
					for(int i = 0; i < buttons.length; i++)
					{
						selected[i] = false;
						buttons[i].updateColor();
					}
					update();
				}
				
				@Override
				public boolean isLocked()
				{
					return ColorsSetting.this.isLocked();
				}
			});
	}
	
	@Override
	public final ArrayList<PossibleKeybind> getPossibleKeybinds(
		String featureName)
	{
		return new ArrayList<>();
	}
	
	@Override
	public final boolean[] getSelected()
	{
		return isLocked() ? lock.getSelected() : selected;
	}
	
	public final void setSelected(int index, boolean selected)
	{
		if(isLocked())
			return;
		
		this.selected[index] = selected;
		update();
	}
	
	public final void lock(ColorsLock lock)
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
	
	@Override
	public final Component getComponent()
	{
		return null;
	}
	
	@Override
	public final void fromJson(JsonElement json)
	{
		if(!json.isJsonArray())
			return;
		
		JsonArray array = json.getAsJsonArray();
		if(array.size() != selected.length)
			return;
		
		for(int i = 0; i < selected.length; i++)
		{
			JsonElement element = array.get(i);
			if(!element.isJsonPrimitive())
				continue;
			
			JsonPrimitive primitive = element.getAsJsonPrimitive();
			if(!primitive.isBoolean())
				continue;
			
			selected[i] = primitive.getAsBoolean();
		}
		
		update();
	}
	
	@Override
	public final JsonElement toJson()
	{
		return JsonUtils.gson.toJsonTree(selected);
	}
	
	@Override
	public final void legacyFromJson(JsonObject json)
	{
		try
		{
			JsonArray jsonColors = json.get(getName()).getAsJsonArray();
			for(int i = 0; i < selected.length; i++)
				selected[i] = jsonColors.get(i).getAsBoolean();
		}catch(Exception e)
		{
			
		}
		
		update();
	}
}
