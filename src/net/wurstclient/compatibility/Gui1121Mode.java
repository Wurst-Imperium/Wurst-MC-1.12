/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.compatibility;

import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.wurstclient.WurstClient;
import net.wurstclient.files.ConfigFiles;
import net.wurstclient.update.Version;

public class Gui1121Mode extends GuiScreen
{
	private final GuiScreen prevScreen;
	
	public Gui1121Mode(GuiScreen prevScreen)
	{
		this.prevScreen = prevScreen;
	}
	
	@Override
	public void initGui()
	{
		buttonList.add(new GuiButton(0, width / 2 - 150, height / 3 * 2, 100,
			20, "MC 1.12"));
		buttonList.add(new GuiButton(1, width / 2 - 50, height / 3 * 2, 100, 20,
			"MC 1.12.1"));
		buttonList.add(new GuiButton(2, width / 2 + 50, height / 3 * 2, 100, 20,
			"MC 1.12.2"));
		
		buttonList.add(
			new GuiButton(-1, width / 2 - 100, height / 3 * 2 + 40, "Cancel"));
		
		int version = WurstClient.INSTANCE.options.mc112x_compatibility;
		if(version >= 0 && version < buttonList.size() - 1)
			buttonList.get(version).enabled = false;
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException
	{
		switch(button.id)
		{
			case 0:
			case 1:
			case 2:
			WurstClient.INSTANCE.options.mc112x_compatibility = button.id;
			ConfigFiles.OPTIONS.save();
			mc.shutdown();
			break;
			
			case -1:
			mc.displayGuiScreen(prevScreen);
			break;
		}
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		drawDefaultBackground();
		drawCenteredString(fontRendererObj,
			"Minecraft 1.12.X Compatibility Mode", width / 2, 20, 0xffffff);
		
		drawCenteredString(fontRendererObj,
			"§aCurrent version: " + new Version(
				"1.12." + WurstClient.INSTANCE.options.mc112x_compatibility),
			width / 2, 80, 0xa0a0a0);
		
		drawCenteredString(fontRendererObj,
			"Only one version can be selected at any time.", width / 2, 110,
			0xa0a0a0);
		drawCenteredString(fontRendererObj,
			"Changing this option requires the game to be restarted.",
			width / 2, 120, 0xa0a0a0);
		
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
}
