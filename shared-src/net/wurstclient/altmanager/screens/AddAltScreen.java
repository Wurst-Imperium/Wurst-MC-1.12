/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.altmanager.screens;

import net.minecraft.client.gui.GuiScreen;
import net.wurstclient.altmanager.Alt;
import net.wurstclient.altmanager.LoginManager;
import net.wurstclient.files.ConfigFiles;

public final class AddAltScreen extends AltEditorScreen
{
	public AddAltScreen(GuiScreen prevScreen)
	{
		super(prevScreen);
	}
	
	@Override
	protected String getTitle()
	{
		return "New Alt";
	}
	
	@Override
	protected String getDoneButtonText()
	{
		return "Add";
	}
	
	@Override
	protected void pressDoneButton()
	{
		if(getPassword().isEmpty())
		{
			// add cracked alt
			message = "";
			GuiAltList.alts.add(new Alt(getEmail(), null, null));
			
		}else
		{
			// add premium alt
			message = LoginManager.login(getEmail(), getPassword());
			
			if(message.isEmpty())
				GuiAltList.alts.add(new Alt(getEmail(), getPassword(),
					mc.session.getUsername()));
		}
		
		if(message.isEmpty())
		{
			GuiAltList.sortAlts();
			ConfigFiles.ALTS.save();
			mc.displayGuiScreen(prevScreen);
			
		}else
			doErrorEffect();
	}
}
