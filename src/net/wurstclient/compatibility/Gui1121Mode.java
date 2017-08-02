/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
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
		boolean mc1121mode =
			WurstClient.INSTANCE.options.experimental_mc1121_mode;
		buttonList.add(new GuiButton(0, width / 2 - 100, height / 3 * 2,
			(mc1121mode ? "§a§lDisable" : "§4§lEnable") + " MC 1.12.1 Mode"));
		buttonList.add(
			new GuiButton(1, width / 2 - 100, height / 3 * 2 + 20, "Cancel"));
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException
	{
		switch(button.id)
		{
			case 0:
			WurstClient.INSTANCE.options.experimental_mc1121_mode =
				!WurstClient.INSTANCE.options.experimental_mc1121_mode;
			ConfigFiles.OPTIONS.save();
			mc.shutdown();
			break;
			
			case 1:
			mc.displayGuiScreen(prevScreen);
			break;
		}
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		drawDefaultBackground();
		drawCenteredString(fontRendererObj,
			"Minecraft 1.12.1 Compatibility Mode", width / 2, 20, 0xffffff);
		
		drawCenteredString(fontRendererObj,
			"§c§lWARNING:§r MC 1.12.1 Mode is highly experimental and may crash at any time.",
			width / 2, 80, 0xa0a0a0);
		
		drawCenteredString(fontRendererObj,
			"Enabling this option allows you to connect to Minecraft 1.12.1 servers.",
			width / 2, 100, 0xa0a0a0);
		drawCenteredString(fontRendererObj,
			"Enabling 1.12.1 compatibility disables 1.12 compatibility.",
			width / 2, 110, 0xa0a0a0);
		drawCenteredString(fontRendererObj,
			"Changing this option requires the game to be restarted.",
			width / 2, 120, 0xa0a0a0);
		
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
}
