/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.altmanager;

import java.io.IOException;
import java.util.HashSet;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public final class AltRenderer
{
	private static final Minecraft mc = Minecraft.getMinecraft();
	private static final HashSet<String> loadedSkins = new HashSet<>();
	
	private static void bindSkinTexture(String name)
	{
		ResourceLocation location = AbstractClientPlayer.getLocationSkin(name);
		
		if(loadedSkins.contains(name))
		{
			mc.getTextureManager().bindTexture(location);
			return;
		}
		
		try
		{
			ThreadDownloadImageData img =
				AbstractClientPlayer.getDownloadImageSkin(location, name);
			img.loadTexture(mc.getResourceManager());
			
		}catch(IOException e)
		{
			e.printStackTrace();
		}
		
		mc.getTextureManager().bindTexture(location);
		loadedSkins.add(name);
	}
	
	public static void drawAltFace(String name, int x, int y, int w, int h,
		boolean selected)
	{
		try
		{
			bindSkinTexture(name);
			GL11.glEnable(GL11.GL_BLEND);
			
			if(selected)
				GL11.glColor4f(1, 1, 1, 1);
			else
				GL11.glColor4f(0.9F, 0.9F, 0.9F, 1);
			
			// Face
			float fw = 192;
			float fh = 192;
			float u = 24;
			float v = 24;
			Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
			
			// Hat
			fw = 192;
			fh = 192;
			u = 120;
			v = 24;
			Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
			
			GL11.glDisable(GL11.GL_BLEND);
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void drawAltBody(String name, int x, int y, int width,
		int height)
	{
		try
		{
			bindSkinTexture(name);
			
			boolean slim = DefaultPlayerSkin
				.getSkinType(EntityPlayer.getOfflineUUID(name)).equals("slim");
			
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glColor4f(1, 1, 1, 1);
			
			// Face
			x = x + width / 4;
			y = y + 0;
			int w = width / 2;
			int h = height / 4;
			float fw = height * 2;
			float fh = height * 2;
			float u = height / 4;
			float v = height / 4;
			Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
			
			// Hat
			x = x + 0;
			y = y + 0;
			w = width / 2;
			h = height / 4;
			u = height / 4 * 5;
			v = height / 4;
			Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
			
			// Chest
			x = x + 0;
			y = y + height / 4;
			w = width / 2;
			h = height / 8 * 3;
			u = height / 4 * 2.5F;
			v = height / 4 * 2.5F;
			Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
			
			// Jacket
			x = x + 0;
			y = y + 0;
			w = width / 2;
			h = height / 8 * 3;
			u = height / 4 * 2.5F;
			v = height / 4 * 4.5F;
			Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
			
			// Left Arm
			x = x - width / 16 * (slim ? 3 : 4);
			y = y + (slim ? height / 32 : 0);
			w = width / 16 * (slim ? 3 : 4);
			h = height / 8 * 3;
			u = height / 4 * 5.5F;
			v = height / 4 * 2.5F;
			Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
			
			// Left Sleeve
			x = x + 0;
			y = y + 0;
			w = width / 16 * (slim ? 3 : 4);
			h = height / 8 * 3;
			u = height / 4 * 5.5F;
			v = height / 4 * 4.5F;
			Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
			
			// Right Arm
			x = x + width / 16 * (slim ? 11 : 12);
			y = y + 0;
			w = width / 16 * (slim ? 3 : 4);
			h = height / 8 * 3;
			u = height / 4 * 5.5F;
			v = height / 4 * 2.5F;
			Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
			
			// Right Sleeve
			x = x + 0;
			y = y + 0;
			w = width / 16 * (slim ? 3 : 4);
			h = height / 8 * 3;
			u = height / 4 * 5.5F;
			v = height / 4 * 4.5F;
			Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
			
			// Left Leg
			x = x - width / 2;
			y = y + height / 32 * (slim ? 11 : 12);
			w = width / 4;
			h = height / 8 * 3;
			u = height / 4 * 0.5F;
			v = height / 4 * 2.5F;
			Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
			
			// Left Pants
			x = x + 0;
			y = y + 0;
			w = width / 4;
			h = height / 8 * 3;
			u = height / 4 * 0.5F;
			v = height / 4 * 4.5F;
			Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
			
			// Right Leg
			x = x + width / 4;
			y = y + 0;
			w = width / 4;
			h = height / 8 * 3;
			u = height / 4 * 0.5F;
			v = height / 4 * 2.5F;
			Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
			
			// Right Pants
			x = x + 0;
			y = y + 0;
			w = width / 4;
			h = height / 8 * 3;
			u = height / 4 * 0.5F;
			v = height / 4 * 4.5F;
			Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
			
			GL11.glDisable(GL11.GL_BLEND);
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void drawAltBack(String name, int x, int y, int width,
		int height)
	{
		try
		{
			bindSkinTexture(name);
			
			boolean slim = DefaultPlayerSkin
				.getSkinType(EntityPlayer.getOfflineUUID(name)).equals("slim");
			
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glColor4f(1, 1, 1, 1);
			
			// Face
			x = x + width / 4;
			y = y + 0;
			int w = width / 2;
			int h = height / 4;
			float fw = height * 2;
			float fh = height * 2;
			float u = height / 4 * 3;
			float v = height / 4;
			Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
			
			// Hat
			x = x + 0;
			y = y + 0;
			w = width / 2;
			h = height / 4;
			u = height / 4 * 7;
			v = height / 4;
			Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
			
			// Chest
			x = x + 0;
			y = y + height / 4;
			w = width / 2;
			h = height / 8 * 3;
			u = height / 4 * 4;
			v = height / 4 * 2.5F;
			Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
			
			// Jacket
			x = x + 0;
			y = y + 0;
			w = width / 2;
			h = height / 8 * 3;
			u = height / 4 * 4;
			v = height / 4 * 4.5F;
			Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
			
			// Left Arm
			x = x - width / 16 * (slim ? 3 : 4);
			y = y + (slim ? height / 32 : 0);
			w = width / 16 * (slim ? 3 : 4);
			h = height / 8 * 3;
			u = height / 4 * (slim ? 6.375F : 6.5F);
			v = height / 4 * 2.5F;
			Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
			
			// Left Sleeve
			x = x + 0;
			y = y + 0;
			w = width / 16 * (slim ? 3 : 4);
			h = height / 8 * 3;
			u = height / 4 * (slim ? 6.375F : 6.5F);
			v = height / 4 * 4.5F;
			Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
			
			// Right Arm
			x = x + width / 16 * (slim ? 11 : 12);
			y = y + 0;
			w = width / 16 * (slim ? 3 : 4);
			h = height / 8 * 3;
			u = height / 4 * (slim ? 6.375F : 6.5F);
			v = height / 4 * 2.5F;
			Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
			
			// Right Sleeve
			x = x + 0;
			y = y + 0;
			w = width / 16 * (slim ? 3 : 4);
			h = height / 8 * 3;
			u = height / 4 * (slim ? 6.375F : 6.5F);
			v = height / 4 * 4.5F;
			Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
			
			// Left Leg
			x = x - width / 2;
			y = y + height / 32 * (slim ? 11 : 12);
			w = width / 4;
			h = height / 8 * 3;
			u = height / 4 * 1.5F;
			v = height / 4 * 2.5F;
			Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
			
			// Left Pants
			x = x + 0;
			y = y + 0;
			w = width / 4;
			h = height / 8 * 3;
			u = height / 4 * 1.5F;
			v = height / 4 * 4.5F;
			Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
			
			// Right Leg
			x = x + width / 4;
			y = y + 0;
			w = width / 4;
			h = height / 8 * 3;
			u = height / 4 * 1.5F;
			v = height / 4 * 2.5F;
			Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
			
			// Right Pants
			x = x + 0;
			y = y + 0;
			w = width / 4;
			h = height / 8 * 3;
			u = height / 4 * 1.5F;
			v = height / 4 * 4.5F;
			Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
			
			GL11.glDisable(GL11.GL_BLEND);
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
