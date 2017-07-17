/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.settings;

import java.awt.Color;
import java.util.ArrayList;

import com.google.gson.JsonObject;

import net.wurstclient.files.ConfigFiles;
import net.wurstclient.keybinds.PossibleKeybind;
import net.wurstclient.navigator.NavigatorFeatureScreen;
import net.wurstclient.navigator.NavigatorFeatureScreen.ButtonData;

public class ModeSetting implements Setting
{
	private String name;
	private String[] modes;
	private int selected;
	private ButtonData[] buttons;
	
	private boolean locked;
	private int lockSelected;
	
	public ModeSetting(String name, String[] modes, int selected)
	{
		this.name = name;
		this.modes = modes;
		this.selected = selected;
	}
	
	@Override
	public final String getName()
	{
		return name;
	}
	
	@Override
	public final void addToFeatureScreen(NavigatorFeatureScreen featureScreen)
	{
		// heading
		featureScreen.addText("\n" + name + ":");
		
		// buttons
		int y = 0;
		buttons = new ButtonData[modes.length];
		for(int i = 0; i < modes.length; i++)
		{
			int x = featureScreen.getMiddleX();
			switch(i % 3)
			{
				case 0:
				x -= 150;
				y = 60 + featureScreen.getTextHeight() + 3;
				featureScreen.addText("\n\n");
				break;
				case 1:
				x -= 49;
				break;
				case 2:
				x += 52;
				break;
			}
			final int iFinal = i;
			ButtonData button = featureScreen.new ButtonData(x, y, 97, 14,
				modes[i], i == getSelected() ? 0x00ff00 : 0x404040)
			{
				@Override
				public void press()
				{
					setSelected(iFinal);
					ConfigFiles.NAVIGATOR.save();
				}
				
				@Override
				public boolean isLocked()
				{
					return locked;
				}
			};
			buttons[i] = button;
			featureScreen.addButton(button);
		}
	}
	
	@Override
	public ArrayList<PossibleKeybind> getPossibleKeybinds(String featureName)
	{
		ArrayList<PossibleKeybind> possibleKeybinds = new ArrayList<>();
		String fullName = featureName + " " + name;
		String command = ".setmode " + featureName.toLowerCase() + " "
			+ name.toLowerCase().replace(" ", "_") + " ";
		String description = "Set " + fullName + " to ";
		
		possibleKeybinds
			.add(new PossibleKeybind(command + "next", "Next " + fullName));
		possibleKeybinds
			.add(new PossibleKeybind(command + "prev", "Previous " + fullName));
		
		for(String mode : modes)
			possibleKeybinds.add(new PossibleKeybind(
				command + mode.toLowerCase().replace(" ", "_"),
				description + mode));
		
		return possibleKeybinds;
	}
	
	public final int getSelected()
	{
		return locked ? lockSelected : selected;
	}
	
	public final void setSelected(int selected)
	{
		if(!locked)
		{
			this.selected = selected;
			if(buttons != null)
				for(int i = 0; i < buttons.length; i++)
					buttons[i].color = i == selected ? new Color(0x00ff00)
						: new Color(0x404040);
			update();
		}
	}
	
	public final String[] getModes()
	{
		return modes;
	}
	
	public final String getSelectedMode()
	{
		return modes[getSelected()];
	}
	
	public final void nextMode()
	{
		selected++;
		if(selected >= modes.length)
			selected = 0;
		update();
	}
	
	public final void prevMode()
	{
		selected--;
		if(selected <= -1)
			selected = modes.length - 1;
		update();
	}
	
	public final int indexOf(String mode)
	{
		for(int i = 0; i < modes.length; i++)
			if(modes[i].equalsIgnoreCase(mode))
				return i;
			
		return -1;
	}
	
	public final void lock(int lockSelected)
	{
		this.lockSelected = lockSelected;
		if(buttons != null)
			for(int i = 0; i < buttons.length; i++)
				buttons[i].color = i == lockSelected ? new Color(0x00ff00)
					: new Color(0x404040);
		locked = true;
		update();
	}
	
	public final void unlock()
	{
		locked = false;
		setSelected(selected);
	}
	
	public final boolean isLocked()
	{
		return locked;
	}
	
	@Override
	public final void save(JsonObject json)
	{
		json.addProperty(name, selected);
	}
	
	@Override
	public final void load(JsonObject json)
	{
		int selected = this.selected;
		
		try
		{
			selected = json.get(name).getAsInt();
		}catch(Exception e)
		{
			
		}
		
		setSelected(selected);
	}
	
	@Override
	public void update()
	{
		
	}
}
