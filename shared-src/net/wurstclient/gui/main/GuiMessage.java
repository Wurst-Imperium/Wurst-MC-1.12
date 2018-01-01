/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.gui.main;

import java.io.IOException;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNo;
import net.wurstclient.WurstClient;
import net.wurstclient.utils.MiscUtils;

public class GuiMessage extends GuiScreen
{
	private String title;
	private String body;
	private JsonObject buttons;
	private String cancel;
	
	public GuiMessage(JsonObject json)
	{
		title = json.get("title").getAsString();
		body = json.get("body").getAsString();
		buttons = json.get("buttons").getAsJsonObject();
		cancel = json.get("cancel").getAsString();
	}
	
	@Override
	public void initGui()
	{
		int i = 0;
		for(Entry<String, JsonElement> entry : buttons.entrySet())
		{
			buttonList.add(new GuiButton(i, width / 2 - 100,
				height / 3 * 2 + i * 24, 200, 20, entry.getKey()));
			i++;
		}
		
		if(cancel.equals("allowed") || cancel.equals("prompt"))
			buttonList.add(new GuiButton(i, width / 2 - 50,
				height / 3 * 2 + i * 24, 100, 20, "Cancel"));
		
		WurstClient.INSTANCE.analytics
			.trackPageView("/message/v" + WurstClient.VERSION, title);
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException
	{
		if(button.id == buttons.entrySet().size())
		{
			if(cancel.equals("allowed"))
				mc.displayGuiScreen(new GuiWurstMainMenu());
			else if(cancel.equals("prompt"))
				mc.displayGuiScreen(new GuiYesNo(this,
					"Are you sure you want to cancel?", "", 0));
		}else
		{
			MiscUtils.openLink(buttons.get(button.displayString).getAsString());
			WurstClient.INSTANCE.analytics.trackEvent("message", "click",
				"v" + WurstClient.VERSION, button.id);
		}
	}
	
	@Override
	public void confirmClicked(boolean result, int id)
	{
		super.confirmClicked(result, id);
		
		if(result)
		{
			mc.displayGuiScreen(new GuiWurstMainMenu());
			WurstClient.INSTANCE.analytics.trackEvent("message", "cancel",
				"v" + WurstClient.VERSION);
		}else
			mc.displayGuiScreen(this);
		
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		drawDefaultBackground();
		
		drawCenteredString(fontRendererObj, title, width / 2, height / 4,
			0xffffffff);
		
		int i = 0;
		for(String line : body.split("\n"))
		{
			drawCenteredString(fontRendererObj, line, width / 2,
				height / 4 + 16 + i * 12, 0xffffffff);
			i++;
		}
		
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
}
