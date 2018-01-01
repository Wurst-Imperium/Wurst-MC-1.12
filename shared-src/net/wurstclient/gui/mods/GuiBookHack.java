/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.gui.mods;

import java.io.IOException;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.client.gui.GuiTextField;

public class GuiBookHack extends GuiScreen
{
	private GuiScreenBook prevScreen;
	private GuiTextField commandBox;
	
	public GuiBookHack(GuiScreenBook prevScreen)
	{
		this.prevScreen = prevScreen;
	}
	
	@Override
	public void initGui()
	{
		Keyboard.enableRepeatEvents(true);
		
		buttonList.add(
			new GuiButton(0, width / 2 - 100, height / 3 * 2, 200, 20, "Done"));
		buttonList.add(new GuiButton(1, width / 2 - 100, height / 3 * 2 + 24,
			200, 20, "Cancel"));
		
		commandBox =
			new GuiTextField(0, fontRendererObj, width / 2 - 100, 60, 200, 20);
		commandBox.setMaxStringLength(100);
		commandBox.setFocused(true);
		commandBox.setText("/");
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException
	{
		if(!button.enabled)
			return;
		
		switch(button.id)
		{
			case 0:
			prevScreen.signWithCommand(commandBox.getText());
			break;
			
			case 1:
			mc.displayGuiScreen(prevScreen);
			break;
		}
	}
	
	@Override
	public void updateScreen()
	{
		commandBox.updateCursorCounter();
	}
	
	@Override
	protected void mouseClicked(int x, int y, int button) throws IOException
	{
		super.mouseClicked(x, y, button);
		commandBox.mouseClicked(x, y, button);
	}
	
	@Override
	protected void keyTyped(char ch, int keyCode)
	{
		commandBox.textboxKeyTyped(ch, keyCode);
	}
	
	@Override
	public void onGuiClosed()
	{
		Keyboard.enableRepeatEvents(false);
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		drawDefaultBackground();
		drawCenteredString(fontRendererObj, "BookHack", width / 2, 20,
			0xffffff);
		
		drawString(fontRendererObj, "Command", width / 2 - 100, 47, 0xa0a0a0);
		
		drawCenteredString(fontRendererObj,
			"The command you type in here will be", width / 2, 100, 0xa0a0a0);
		drawCenteredString(fontRendererObj,
			"executed by anyone who clicks the text", width / 2, 110, 0xa0a0a0);
		drawCenteredString(fontRendererObj, "in your book.", width / 2, 120,
			0xa0a0a0);
		
		commandBox.drawTextBox();
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
}
