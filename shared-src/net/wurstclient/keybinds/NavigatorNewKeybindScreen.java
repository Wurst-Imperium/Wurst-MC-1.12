/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.keybinds;

import static org.lwjgl.opengl.GL11.GL_SCISSOR_TEST;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;

import java.io.IOException;
import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.GuiButton;
import net.wurstclient.WurstClient;
import net.wurstclient.files.ConfigFiles;
import net.wurstclient.font.Fonts;
import net.wurstclient.navigator.NavigatorFeatureScreen;
import net.wurstclient.navigator.NavigatorScreen;
import net.wurstclient.utils.RenderUtils;

public class NavigatorNewKeybindScreen extends NavigatorScreen
{
	private ArrayList<PossibleKeybind> possibleKeybinds;
	private NavigatorFeatureScreen parent;
	private int hoveredCommand = -1;
	private int selectedCommand = -1;
	private String selectedKey = "NONE";
	private String text;
	private GuiButton okButton;
	private boolean choosingKey;
	
	public NavigatorNewKeybindScreen(
		ArrayList<PossibleKeybind> possibleKeybinds,
		NavigatorFeatureScreen parent)
	{
		this.possibleKeybinds = possibleKeybinds;
		this.parent = parent;
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException
	{
		if(!button.enabled)
			return;
		
		switch(button.id)
		{
			case 0:
			if(choosingKey)
			{
				String newCommands =
					possibleKeybinds.get(selectedCommand).getCommand();
				
				String oldCommands =
					WurstClient.INSTANCE.getKeybinds().getCommands(selectedKey);
				if(oldCommands != null)
					newCommands = oldCommands + " ; " + newCommands;
				
				WurstClient.INSTANCE.getKeybinds().add(selectedKey,
					newCommands);
				
				WurstClient.INSTANCE.navigator
					.addPreference(parent.getFeature().getName());
				ConfigFiles.NAVIGATOR.save();
				mc.displayGuiScreen(parent);
			}else
			{
				choosingKey = true;
				okButton.enabled = false;
			}
			break;
			case 1:
			mc.displayGuiScreen(parent);
			break;
		}
	}
	
	@Override
	protected void onResize()
	{
		// OK button
		okButton =
			new GuiButton(0, width / 2 - 151, height - 65, 149, 18, "OK");
		okButton.enabled = selectedCommand != -1;
		buttonList.add(okButton);
		
		// cancel button
		buttonList.add(
			new GuiButton(1, width / 2 + 2, height - 65, 149, 18, "Cancel"));
	}
	
	@Override
	protected void onKeyPress(char typedChar, int keyCode)
	{
		if(choosingKey)
		{
			selectedKey = Keyboard.getKeyName(keyCode);
			okButton.enabled = !selectedKey.equals("NONE");
		}else if(keyCode == 1)
			mc.displayGuiScreen(parent);
	}
	
	@Override
	protected void onMouseClick(int x, int y, int button)
	{
		// commands
		if(hoveredCommand != -1)
		{
			selectedCommand = hoveredCommand;
			okButton.enabled = true;
		}
	}
	
	@Override
	protected void onMouseDrag(int x, int y, int button, long timeDragged)
	{
		
	}
	
	@Override
	protected void onMouseRelease(int x, int y, int button)
	{
		
	}
	
	@Override
	protected void onUpdate()
	{
		// text
		if(choosingKey)
		{
			text = "Now press the key that should trigger this keybind.";
			if(!selectedKey.equals("NONE"))
			{
				text += "\n\nKey: " + selectedKey;
				String commands =
					WurstClient.INSTANCE.getKeybinds().getCommands(selectedKey);
				if(commands != null)
				{
					text +=
						"\n\nWARNING: This key is already bound to the following command(s):";
					commands = commands.replace(";", "§").replace("§§", ";");
					
					for(String cmd : commands.split("§"))
						text += "\n- " + cmd;
				}
			}
		}else
			text = "Select what this keybind should do.";
		
		// content height
		if(choosingKey)
			setContentHeight(Fonts.segoe15.getStringHeight(text));
		else
			setContentHeight(possibleKeybinds.size() * 24 - 10);
	}
	
	@Override
	protected void onRender(int mouseX, int mouseY, float partialTicks)
	{
		// title bar
		drawCenteredString(Fonts.segoe22, "New Keybind", middleX, 32, 0xffffff);
		glDisable(GL_TEXTURE_2D);
		
		// background
		int bgx1 = middleX - 154;
		int bgx2 = middleX + 154;
		int bgy1 = 60;
		int bgy2 = height - 43;
		
		// scissor box
		RenderUtils.scissorBox(bgx1, bgy1, bgx2,
			bgy2 - (buttonList.isEmpty() ? 0 : 24));
		glEnable(GL_SCISSOR_TEST);
		
		// possible keybinds
		if(!choosingKey)
		{
			hoveredCommand = -1;
			int yi = bgy1 - 12 + scroll;
			for(int i = 0; i < possibleKeybinds.size(); i++)
			{
				yi += 24;
				PossibleKeybind possibleKeybind = possibleKeybinds.get(i);
				
				// positions
				int x1 = bgx1 + 2;
				int x2 = bgx2 - 2;
				int y1 = yi;
				int y2 = y1 + 20;
				
				// color
				if(mouseX >= x1 && mouseX <= x2 && mouseY >= y1 && mouseY <= y2
					&& mouseY <= bgy2 - 24)
				{
					hoveredCommand = i;
					if(i == selectedCommand)
						glColor4f(0F, 1F, 0F, 0.375F);
					else
						glColor4f(0.25F, 0.25F, 0.25F, 0.375F);
				}else if(i == selectedCommand)
					glColor4f(0F, 1F, 0F, 0.25F);
				else
					glColor4f(0.25F, 0.25F, 0.25F, 0.25F);
				
				// button
				drawBox(x1, y1, x2, y2);
				
				// text
				drawString(Fonts.segoe15,
					possibleKeybind.getDescription() + "\n"
						+ possibleKeybind.getCommand(),
					x1 + 1, y1 - 1, 0xffffff);
				glDisable(GL_TEXTURE_2D);
			}
		}
		
		// text
		drawString(Fonts.segoe15, text, bgx1 + 2, bgy1 + scroll, 0xffffff);
		
		// scissor box
		glDisable(GL_SCISSOR_TEST);
		
		// buttons below scissor box
		for(int i = 0; i < buttonList.size(); i++)
		{
			GuiButton button = buttonList.get(i);
			
			// positions
			int x1 = button.xPosition;
			int x2 = x1 + button.getButtonWidth();
			int y1 = button.yPosition;
			int y2 = y1 + 18;
			
			// color
			if(!button.enabled)
				glColor4f(0F, 0F, 0F, 0.25F);
			else if(mouseX >= x1 && mouseX <= x2 && mouseY >= y1
				&& mouseY <= y2)
				glColor4f(0.375F, 0.375F, 0.375F, 0.25F);
			else
				glColor4f(0.25F, 0.25F, 0.25F, 0.25F);
			
			// button
			glDisable(GL_TEXTURE_2D);
			drawBox(x1, y1, x2, y2);
			
			// text
			drawCenteredString(Fonts.segoe18, button.displayString,
				(x1 + x2) / 2, y1 + 2, 0xffffff);
		}
	}
}
