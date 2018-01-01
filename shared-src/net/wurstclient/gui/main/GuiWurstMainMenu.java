/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.gui.main;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glEnable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Calendar;

import javax.net.ssl.HttpsURLConnection;

import org.lwjgl.opengl.GL11;

import com.google.gson.JsonObject;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.wurstclient.WurstClient;
import net.wurstclient.altmanager.screens.AltManagerScreen;
import net.wurstclient.compatibility.WMath;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.files.ConfigFiles;
import net.wurstclient.update.Version;
import net.wurstclient.utils.JsonUtils;
import net.wurstclient.utils.MiscUtils;

public class GuiWurstMainMenu extends GuiMainMenu
{
	private static final ResourceLocation title =
		new ResourceLocation("wurst/wurst_380.png");
	private static final ResourceLocation santaHat =
		new ResourceLocation("wurst/santa_hat.png");
	private static boolean startupMessageDisabled = false;
	
	private String noticeText = "";
	private String noticeLink =
		"https://www.wurstclient.net/download/minecraft-1-11-x/";
	
	private int noticeWidth2;
	private int noticeWidth1;
	private int noticeX1;
	private int noticeY1;
	private int noticeX2;
	private int noticeY2;
	
	public GuiWurstMainMenu()
	{
		super();
	}
	
	@Override
	public void initGui()
	{
		super.initGui();
		
		// adjust position of options, quit & language buttons
		for(GuiButton button : buttonList)
			button.yPosition = Math.min(button.yPosition, height - 56);
		
		// notice
		noticeWidth1 = fontRendererObj.getStringWidth(noticeText);
		noticeWidth2 =
			fontRendererObj.getStringWidth(GuiMainMenu.MORE_INFO_TEXT);
		int noticeWidth = Math.max(noticeWidth1, noticeWidth2);
		noticeX1 = (width - noticeWidth) / 2;
		noticeY1 = buttonList.get(0).yPosition - 24;
		noticeX2 = noticeX1 + noticeWidth;
		noticeY2 = noticeY1 + 24;
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException
	{
		super.actionPerformed(button);
		
		switch(button.id)
		{
			case 3:
			mc.displayGuiScreen(new AltManagerScreen(this));
			break;
		}
	}
	
	@Override
	public void confirmClicked(boolean result, int id)
	{
		super.confirmClicked(result, id);
		
		// changelog
		if(id == 64)
		{
			if(result)
				WurstClient.INSTANCE.analytics.trackEvent("changelog",
					"go play");
			else
			{
				MiscUtils.openLink(
					new Version(WurstClient.VERSION).getChangelogLink());
				WurstClient.INSTANCE.analytics.trackEvent("changelog",
					"view changelog");
			}
			mc.displayGuiScreen(this);
		}
	}
	
	@Override
	public void updateScreen()
	{
		super.updateScreen();
		
		// updater
		if(startupMessageDisabled)
			return;
		if(WurstClient.INSTANCE.updater.isOutdated())
		{
			WurstClient.INSTANCE.analytics.trackEvent("updater",
				"update to v" + WurstClient.INSTANCE.updater.getLatestVersion(),
				"from " + WurstClient.VERSION);
			WurstClient.INSTANCE.updater.update();
			startupMessageDisabled = true;
		}
		
		// emergency message
		if(startupMessageDisabled)
			return;
		try
		{
			HttpsURLConnection connection = (HttpsURLConnection)new URL(
				"https://www.wurstclient.net/api/v1/messages.json")
					.openConnection();
			connection.connect();
			
			JsonObject json = JsonUtils.jsonParser
				.parse(
					new InputStreamReader(connection.getInputStream(), "UTF-8"))
				.getAsJsonObject();
			
			if(json.get(WurstClient.VERSION) != null)
			{
				System.out.println("Emergency message found!");
				mc.displayGuiScreen(new GuiMessage(
					json.get(WurstClient.VERSION).getAsJsonObject()));
				startupMessageDisabled = true;
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		// changelog
		if(startupMessageDisabled)
			return;
		if(!WurstClient.VERSION
			.equals(WurstClient.INSTANCE.options.lastLaunchedVersion))
		{
			mc.displayGuiScreen(new GuiYesNo(this,
				"Successfully updated to Wurst v" + WurstClient.VERSION, "",
				"Go Play", "View Changelog", 64));
			WurstClient.INSTANCE.options.lastLaunchedVersion =
				WurstClient.VERSION;
			ConfigFiles.OPTIONS.save();
		}
		
		startupMessageDisabled = true;
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		if(!WurstClient.INSTANCE.isEnabled())
		{
			super.drawScreen(mouseX, mouseY, partialTicks);
			return;
		}
		
		// panorama
		GlStateManager.disableAlpha();
		renderSkybox(mouseX, mouseY, partialTicks);
		GlStateManager.enableAlpha();
		drawGradientRect(0, 0, width, height, -2130706433, 16777215);
		drawGradientRect(0, 0, width, height, 0, Integer.MIN_VALUE);
		
		// title image
		mc.getTextureManager().bindTexture(title);
		GlStateManager.color(1, 1, 1, 1);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		int x = width / 2 - 256 / 2;
		int y = 36;
		int w = 256;
		int h = 64;
		float fw = 256;
		float fh = 64;
		float u = 0;
		float v = 0;
		if(GuiMainMenu.splashText.equals("umop-apisdn!"))
		{
			GL11.glRotatef(180, 0, 0, 1);
			GL11.glTranslatef(-width, -h - 60, 0);
		}
		drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
		if(Calendar.getInstance().get(Calendar.MONTH) == Calendar.DECEMBER)
		{
			mc.getTextureManager().bindTexture(santaHat);
			x = x + 112;
			y = y - 36;
			h = 48;
			w = 48;
			fw = 48;
			fh = 48;
			u = 0;
			v = 0;
			drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
		}
		if(GuiMainMenu.splashText.equals("umop-apisdn!"))
		{
			GL11.glRotatef(-180, 0, 0, 1);
			GL11.glTranslatef(-width, -h - 60, 0);
		}
		
		// splash text
		GlStateManager.pushMatrix();
		GlStateManager.translate(width / 2 + 90, 72.0F, 0.0F);
		GlStateManager.rotate(-20.0F, 0.0F, 0.0F, 1.0F);
		float splashScale = 1.8F - Math.abs(WMath.sin(
			Minecraft.getSystemTime() % 1000L / 1000F * (float)Math.PI * 2.0F)
			* 0.1F);
		splashScale = splashScale * 100.0F
			/ (fontRendererObj.getStringWidth(splashText) + 32);
		GlStateManager.scale(splashScale, splashScale, splashScale);
		
		drawCenteredString(fontRendererObj, splashText, 0, 0, -256);
		GlStateManager.popMatrix();
		
		// text
		String vMinecraft = "Minecraft " + WMinecraft.DISPLAY_VERSION;
		String cMinecraft1 = "Copyright Mojang AB";
		String cMinecraft2 = "Do not distribute!";
		drawString(fontRendererObj, vMinecraft,
			width - fontRendererObj.getStringWidth(vMinecraft) - 8, 8,
			0xffffff);
		drawString(fontRendererObj, cMinecraft1,
			width - fontRendererObj.getStringWidth(cMinecraft1) - 8, 18,
			0xffffff);
		drawString(fontRendererObj, cMinecraft2,
			width - fontRendererObj.getStringWidth(cMinecraft2) - 8, 28,
			0xffffff);
		drawString(fontRendererObj, "Wurst Client " + WurstClient.VERSION
			+ (WurstClient.INSTANCE.updater.isOutdated() ? " (outdated)" : ""),
			8, 8, 0xffffff);
		drawString(fontRendererObj, "Copyright Alexander01998", 8, 18,
			0xffffff);
		drawString(fontRendererObj, "All rights reserved.", 8, 28, 0xffffff);
		drawCenteredString(fontRendererObj, "§nwww.WurstClient.net", width / 2,
			height - 26, 0xffffff);
		
		// buttons
		for(Object button : buttonList)
			((GuiButton)button).drawButton(mc, mouseX, mouseY);
		
		// notice
		if(noticeText != null && noticeText.length() > 0)
		{
			drawRect(noticeX1 - 2, noticeY1 - 2, noticeX2 + 2, noticeY2 - 1,
				1428160512);
			drawString(fontRendererObj, noticeText, noticeX1, noticeY1, -1);
			drawString(fontRendererObj, GuiMainMenu.MORE_INFO_TEXT,
				(width - noticeWidth2) / 2, buttonList.get(0).yPosition - 12,
				-1);
		}
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton)
		throws IOException
	{
		super.mouseClicked(mouseX, mouseY, mouseButton);
		
		// website link
		int linkWidth = fontRendererObj.getStringWidth("§nwww.WurstClient.net");
		if(mouseButton == 0 && mouseY >= height - 26 && mouseY < height - 16
			&& mouseX > width / 2 - linkWidth / 2
			&& mouseX < width / 2 + linkWidth / 2)
			MiscUtils.openLink("https://www.wurstclient.net/");
		
		// notice
		if(noticeText.length() > 0 && mouseX >= noticeX1 && mouseX <= noticeX2
			&& mouseY >= noticeY1 && mouseY <= noticeY2)
			MiscUtils.openLink(noticeLink);
	}
}
