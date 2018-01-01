/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.keybinds;

import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.wurstclient.WurstClient;
import net.wurstclient.gui.options.GuiPressAKey;
import net.wurstclient.gui.options.GuiPressAKeyCallback;

public final class KeybindEditorScreen extends GuiScreen
	implements GuiPressAKeyCallback
{
	private final GuiScreen prevScreen;
	
	private String key;
	private final String oldKey;
	private final String oldCommands;
	
	private GuiTextField commandField;
	
	public KeybindEditorScreen(GuiScreen prevScreen)
	{
		this.prevScreen = prevScreen;
		
		key = "NONE";
		oldKey = null;
		oldCommands = null;
	}
	
	public KeybindEditorScreen(GuiScreen prevScreen, String key,
		String commands)
	{
		this.prevScreen = prevScreen;
		
		this.key = key;
		oldKey = key;
		oldCommands = commands;
	}
	
	@Override
	public void initGui()
	{
		buttonList.add(new GuiButton(0, width / 2 - 100, 60, "Change Key"));
		buttonList
			.add(new GuiButton(1, width / 2 - 100, height / 4 + 72, "Save"));
		buttonList
			.add(new GuiButton(2, width / 2 - 100, height / 4 + 96, "Cancel"));
		
		commandField =
			new GuiTextField(0, fontRendererObj, width / 2 - 100, 100, 200, 20);
		commandField.setMaxStringLength(65536);
		commandField.setFocused(true);
		
		if(oldCommands != null)
			commandField.setText(oldCommands);
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException
	{
		if(!button.enabled)
			return;
		
		switch(button.id)
		{
			case 0:
			mc.displayGuiScreen(new GuiPressAKey(this));
			break;
			
			case 1:
			if(oldKey != null)
				WurstClient.INSTANCE.getKeybinds().remove(oldKey);
			
			WurstClient.INSTANCE.getKeybinds().add(key, commandField.getText());
			
			mc.displayGuiScreen(prevScreen);
			break;
			
			case 2:
			mc.displayGuiScreen(prevScreen);
			break;
		}
	}
	
	@Override
	protected void mouseClicked(int x, int y, int button) throws IOException
	{
		super.mouseClicked(x, y, button);
		commandField.mouseClicked(x, y, button);
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException
	{
		commandField.textboxKeyTyped(typedChar, keyCode);
	}
	
	@Override
	public void updateScreen()
	{
		commandField.updateCursorCounter();
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		drawBackground(0);
		
		drawCenteredString(fontRendererObj,
			(oldKey != null ? "Edit" : "Add") + " Keybind", width / 2, 20,
			0xffffff);
		
		drawString(fontRendererObj, "Key: " + key, width / 2 - 100, 47,
			0xa0a0a0);
		drawString(fontRendererObj, "Commands (separated by ';')",
			width / 2 - 100, 87, 0xa0a0a0);
		
		commandField.drawTextBox();
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
	
	@Override
	public void setKey(String key)
	{
		this.key = key;
	}
}
