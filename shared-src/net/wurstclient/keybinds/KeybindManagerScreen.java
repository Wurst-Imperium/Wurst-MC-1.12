/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.keybinds;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.gui.GuiYesNo;
import net.wurstclient.WurstClient;
import net.wurstclient.files.ConfigFiles;

public final class KeybindManagerScreen extends GuiScreen
{
	private final GuiScreen prevScreen;
	
	private ListGui listGui;
	private GuiButton addButton;
	private GuiButton editButton;
	private GuiButton removeButton;
	private GuiButton backButton;
	
	public KeybindManagerScreen(GuiScreen prevScreen)
	{
		this.prevScreen = prevScreen;
	}
	
	@Override
	public void initGui()
	{
		listGui = new ListGui(mc, width, height, 36, height - 56, 30);
		
		buttonList.add(addButton =
			new GuiButton(0, width / 2 - 102, height - 52, 100, 20, "Add"));
		buttonList.add(editButton =
			new GuiButton(1, width / 2 + 2, height - 52, 100, 20, "Edit"));
		buttonList.add(removeButton =
			new GuiButton(2, width / 2 - 102, height - 28, 100, 20, "Remove"));
		buttonList.add(backButton =
			new GuiButton(3, width / 2 + 2, height - 28, 100, 20, "Back"));
		buttonList.add(new GuiButton(4, 8, 8, 100, 20, "Reset Keybinds"));
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException
	{
		if(!button.enabled)
			return;
		
		switch(button.id)
		{
			case 0:
			// Add
			mc.displayGuiScreen(new KeybindEditorScreen(this));
			break;
			
			case 1:
			// Edit
			String key = WurstClient.INSTANCE.keybinds.keySet()
				.toArray(new String[WurstClient.INSTANCE.keybinds
					.size()])[listGui.selectedSlot];
			ArrayList<String> commands = WurstClient.INSTANCE.keybinds.get(key);
			mc.displayGuiScreen(new KeybindEditorScreen(this, key, commands));
			break;
			
			case 2:
			// Remove
			String key1 = WurstClient.INSTANCE.keybinds.keySet()
				.toArray(new String[WurstClient.INSTANCE.keybinds
					.size()])[listGui.selectedSlot];
			WurstClient.INSTANCE.keybinds.unbind(key1);
			ConfigFiles.KEYBINDS.save();
			break;
			
			case 3:
			// Back
			WurstClient.INSTANCE.keybinds.forceAddGuiKeybind();
			mc.displayGuiScreen(prevScreen);
			break;
			
			case 4:
			// Reset Keybinds
			mc.displayGuiScreen(new GuiYesNo(this,
				"Are you sure you want to reset your keybinds?",
				"This cannot be undone!", 0));
			break;
		}
	}
	
	@Override
	public void confirmClicked(boolean confirmed, int id)
	{
		if(confirmed)
		{
			WurstClient.INSTANCE.keybinds.loadDefaults();
			ConfigFiles.KEYBINDS.save();
		}
		
		mc.displayGuiScreen(this);
	}
	
	@Override
	public void handleMouseInput() throws IOException
	{
		super.handleMouseInput();
		listGui.handleMouseInput();
	}
	
	@Override
	protected void mouseClicked(int x, int y, int button) throws IOException
	{
		if(y >= 36 && y <= height - 57)
			if(x >= width / 2 + 140 || x <= width / 2 - 126)
				listGui.elementClicked(-1, false, 0, 0);
			
		super.mouseClicked(x, y, button);
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException
	{
		if(keyCode == Keyboard.KEY_RETURN)
			actionPerformed(editButton.enabled ? editButton : addButton);
		else if(keyCode == Keyboard.KEY_ESCAPE)
			actionPerformed(backButton);
	}
	
	@Override
	public void updateScreen()
	{
		boolean inBounds = listGui.selectedSlot > -1
			&& listGui.selectedSlot < listGui.getSize();
		
		editButton.enabled = inBounds;
		removeButton.enabled = inBounds;
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		drawDefaultBackground();
		listGui.drawScreen(mouseX, mouseY, partialTicks);
		
		drawCenteredString(fontRendererObj, "Keybind Manager", width / 2, 8,
			0xffffff);
		drawCenteredString(fontRendererObj, "Keybinds: " + listGui.getSize(),
			width / 2, 20, 0xffffff);
		
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
	
	private static final class ListGui extends GuiSlot
	{
		private int selectedSlot = -1;
		
		public ListGui(Minecraft mc, int width, int height, int top, int bottom,
			int slotHeight)
		{
			super(mc, width, height, top, bottom, slotHeight);
		}
		
		@Override
		protected boolean isSelected(int index)
		{
			return selectedSlot == index;
		}
		
		@Override
		protected int getSize()
		{
			return WurstClient.INSTANCE.keybinds.size();
		}
		
		@Override
		protected void elementClicked(int index, boolean isDoubleClick,
			int mouseX, int mouseY)
		{
			selectedSlot = index;
		}
		
		@Override
		protected void drawBackground()
		{
			
		}
		
		@Override
		protected void drawSlot(int id, int x, int y, int slotHeight,
			int mouseX, int mouseY, float partialTicks)
		{
			Entry entry = WurstClient.INSTANCE.keybinds.entrySet().toArray(
				new Map.Entry[WurstClient.INSTANCE.keybinds.size()])[id];
			
			mc.fontRendererObj.drawString("Key: " + entry.getKey(), x + 3,
				y + 3, 0xa0a0a0);
			mc.fontRendererObj.drawString("Command: " + entry.getValue(), x + 3,
				y + 15, 0xa0a0a0);
		}
	}
}
