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

public final class EditAltScreen extends AltEditorScreen
{
	private Alt editedAlt;
	
	public EditAltScreen(GuiScreen prevScreen, Alt editedAlt)
	{
		super(prevScreen);
		this.editedAlt = editedAlt;
	}
	
	@Override
	protected String getTitle()
	{
		return "Edit Alt";
	}
	
	@Override
	protected String getDefaultEmail()
	{
		return editedAlt.getEmail();
	}
	
	@Override
	protected String getDefaultPassword()
	{
		return editedAlt.getPassword();
	}
	
	@Override
	protected String getDoneButtonText()
	{
		return "Save";
	}
	
	@Override
	protected void pressDoneButton()
	{
		if(getPassword().isEmpty())
		{
			// cracked
			message = "";
			GuiAltList.alts.set(GuiAltList.alts.indexOf(editedAlt),
				new Alt(getEmail(), null, null, editedAlt.isStarred()));
			
		}else
		{
			// premium
			message = LoginManager.login(getEmail(), getPassword());
			if(message.isEmpty())
				GuiAltList.alts.set(GuiAltList.alts.indexOf(editedAlt),
					new Alt(getEmail(), getPassword(), mc.session.getUsername(),
						editedAlt.isStarred()));
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
