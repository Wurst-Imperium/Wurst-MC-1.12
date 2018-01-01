/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import net.wurstclient.WurstClient;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.font.Fonts;

public final class WurstLogo
{
	private static final ResourceLocation texture =
		new ResourceLocation("wurst/wurst_128.png");
	
	public void render()
	{
		String version = getVersionString();
		
		// draw version background
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		if(WurstClient.INSTANCE.mods.rainbowUiMod.isActive())
		{
			float[] acColor = WurstClient.INSTANCE.getGui().getAcColor();
			GL11.glColor4f(acColor[0], acColor[1], acColor[2], 0.5F);
		}else
			GL11.glColor4f(1, 1, 1, 0.5F);
		drawQuads(0, 6, Fonts.segoe22.getStringWidth(version) + 76, 18);
		
		// draw version string
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		Fonts.segoe22.drawString(version, 74, 4, 0xFF000000);
		
		// draw Wurst logo
		GL11.glColor4f(1, 1, 1, 1);
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		Gui.drawModalRectWithCustomSizedTexture(0, 3, 0, 0, 72, 18, 72, 18);
	}
	
	private String getVersionString()
	{
		String version = "v" + WurstClient.VERSION;
		version += " MC" + WMinecraft.VERSION;
		
		if(WMinecraft.OPTIFINE)
			version += " OF";
		
		if(WurstClient.INSTANCE.updater.isOutdated())
			version += " (outdated)";
		
		return version;
	}
	
	private void drawQuads(int x1, int y1, int x2, int y2)
	{
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex2i(x1, y1);
		GL11.glVertex2i(x2, y1);
		GL11.glVertex2i(x2, y2);
		GL11.glVertex2i(x1, y2);
		GL11.glEnd();
	}
}
