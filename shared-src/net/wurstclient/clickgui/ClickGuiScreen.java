/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.clickgui;

import java.io.IOException;

import net.minecraft.client.gui.GuiScreen;

public final class ClickGuiScreen extends GuiScreen
{
	private final ClickGui gui;
	
	public ClickGuiScreen(ClickGui gui)
	{
		this.gui = gui;
	}
	
	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton)
		throws IOException
	{
		super.mouseClicked(mouseX, mouseY, mouseButton);
		gui.handleMouseClick(mouseX, mouseY, mouseButton);
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		super.drawScreen(mouseX, mouseY, partialTicks);
		gui.render(mouseX, mouseY, partialTicks);
	}
}
