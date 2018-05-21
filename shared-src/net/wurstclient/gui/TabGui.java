/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.gui;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.wurstclient.WurstClient;
import net.wurstclient.clickgui.ClickGui;
import net.wurstclient.events.KeyPressListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Feature;
import net.wurstclient.features.special_features.TabGuiSpf;
import net.wurstclient.font.Fonts;

public final class TabGui implements KeyPressListener
{
	private final ArrayList<Tab> tabs = new ArrayList<>();
	private final TabGuiSpf tabGuiSpf = WurstClient.INSTANCE.special.tabGuiSpf;
	
	private int width;
	private int height;
	private int selected;
	private boolean tabOpened;
	
	public TabGui()
	{
		WurstClient.INSTANCE.events.add(KeyPressListener.class, this);
		
		LinkedHashMap<Category, Tab> tabMap = new LinkedHashMap<>();
		for(Category category : Category.values())
			tabMap.put(category, new Tab(category.getName()));
		
		ArrayList<Feature> features = new ArrayList<>();
		features.addAll(WurstClient.INSTANCE.mods.getAllMods());
		features.addAll(WurstClient.INSTANCE.commands.getAllCommands());
		features.addAll(WurstClient.INSTANCE.special.getAllFeatures());
		
		for(Feature feature : features)
			if(feature.getCategory() != null)
				tabMap.get(feature.getCategory()).add(feature);
			
		tabs.addAll(tabMap.values());
		tabs.forEach(tab -> tab.updateSize());
		updateSize();
	}
	
	private void updateSize()
	{
		width = 64;
		for(Tab tab : tabs)
		{
			int tabWidth = Fonts.segoe18.getStringWidth(tab.name) + 10;
			if(tabWidth > width)
				width = tabWidth;
		}
		height = tabs.size() * 10;
	}
	
	@Override
	public void onKeyPress(KeyPressEvent event)
	{
		if(tabGuiSpf.isHidden())
			return;
		
		if(tabOpened)
			switch(event.getKeyCode())
			{
				case Keyboard.KEY_LEFT:
				tabOpened = false;
				break;
				
				default:
				tabs.get(selected).onKeyPress(event.getKeyCode());
				break;
			}
		else
			switch(event.getKeyCode())
			{
				case Keyboard.KEY_DOWN:
				if(selected < tabs.size() - 1)
					selected++;
				else
					selected = 0;
				break;
				
				case Keyboard.KEY_UP:
				if(selected > 0)
					selected--;
				else
					selected = tabs.size() - 1;
				break;
				
				case Keyboard.KEY_RIGHT:
				tabOpened = true;
				break;
			}
	}
	
	public void render(float partialTicks)
	{
		if(tabGuiSpf.isHidden())
			return;
		
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		
		GL11.glPushMatrix();
		ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
		
		int x = 2;
		int y = 23;
		
		GL11.glTranslatef(x, y, 0);
		drawBox(0, 0, width, height);
		
		int factor = sr.getScaleFactor();
		GL11.glScissor(x * factor, (sr.getScaledHeight() - height - y) * factor,
			width * factor, height * factor);
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		
		int textY = -2;
		for(int i = 0; i < tabs.size(); i++)
		{
			String tabName = tabs.get(i).name;
			if(i == selected)
				tabName = (tabOpened ? "<" : ">") + tabName;
			
			Fonts.segoe18.drawString(tabName, 2, textY, 0xffffffff);
			textY += 10;
		}
		
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
		
		if(tabOpened)
		{
			GL11.glPushMatrix();
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			
			Tab tab = tabs.get(selected);
			int tabX = x + width + 2;
			int tabY = y;
			
			GL11.glTranslatef(width + 2, 0, 0);
			drawBox(0, 0, tab.width, tab.height);
			
			GL11.glScissor(tabX * factor,
				(sr.getScaledHeight() - tab.height - tabY) * factor,
				tab.width * factor, tab.height * factor);
			GL11.glEnable(GL11.GL_SCISSOR_TEST);
			
			int tabTextY = -2;
			for(int i = 0; i < tab.features.size(); i++)
			{
				Feature feature = tab.features.get(i);
				String fName = feature.getName();
				
				if(feature.isEnabled())
					if(feature.isBlocked())
						fName = "§c" + fName + "§r";
					else
						fName = "§a" + fName + "§r";
					
				if(i == tab.selected)
					fName = ">" + fName;
				
				Fonts.segoe18.drawString(fName, 2, tabTextY, 0xffffffff);
				tabTextY += 10;
			}
			
			GL11.glDisable(GL11.GL_SCISSOR_TEST);
			
			GL11.glPopMatrix();
		}
		
		GL11.glPopMatrix();
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_CULL_FACE);
	}
	
	private void drawBox(int x1, int y1, int x2, int y2)
	{
		ClickGui gui = WurstClient.INSTANCE.getGui();
		float[] bgColor = gui.getBgColor();
		float[] acColor = gui.getAcColor();
		float opacity = gui.getOpacity();
		
		// color
		GL11.glColor4f(bgColor[0], bgColor[1], bgColor[2], opacity);
		
		// box
		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glVertex2i(x1, y1);
			GL11.glVertex2i(x2, y1);
			GL11.glVertex2i(x2, y2);
			GL11.glVertex2i(x1, y2);
		}
		GL11.glEnd();
		
		// outline positions
		double xi1 = x1 - 0.1;
		double xi2 = x2 + 0.1;
		double yi1 = y1 - 0.1;
		double yi2 = y2 + 0.1;
		
		// outline
		GL11.glLineWidth(1);
		GL11.glColor4f(acColor[0], acColor[1], acColor[2], 0.5F);
		GL11.glBegin(GL11.GL_LINE_LOOP);
		{
			GL11.glVertex2d(xi1, yi1);
			GL11.glVertex2d(xi2, yi1);
			GL11.glVertex2d(xi2, yi2);
			GL11.glVertex2d(xi1, yi2);
		}
		GL11.glEnd();
		
		// shadow positions
		xi1 -= 0.9;
		xi2 += 0.9;
		yi1 -= 0.9;
		yi2 += 0.9;
		
		// top left
		GL11.glBegin(GL11.GL_POLYGON);
		{
			GL11.glColor4f(acColor[0], acColor[1], acColor[2], 0.75F);
			GL11.glVertex2d(x1, y1);
			GL11.glVertex2d(x2, y1);
			GL11.glColor4f(0, 0, 0, 0);
			GL11.glVertex2d(xi2, yi1);
			GL11.glVertex2d(xi1, yi1);
			GL11.glVertex2d(xi1, yi2);
			GL11.glColor4f(acColor[0], acColor[1], acColor[2], 0.75F);
			GL11.glVertex2d(x1, y2);
		}
		GL11.glEnd();
		
		// bottom right
		GL11.glBegin(GL11.GL_POLYGON);
		{
			GL11.glVertex2d(x2, y2);
			GL11.glVertex2d(x2, y1);
			GL11.glColor4f(0, 0, 0, 0);
			GL11.glVertex2d(xi2, yi1);
			GL11.glVertex2d(xi2, yi2);
			GL11.glVertex2d(xi1, yi2);
			GL11.glColor4f(acColor[0], acColor[1], acColor[2], 0.75F);
			GL11.glVertex2d(x1, y2);
		}
		GL11.glEnd();
	}
	
	private static final class Tab
	{
		private final String name;
		private final ArrayList<Feature> features = new ArrayList<>();
		
		private int width;
		private int height;
		private int selected;
		
		public Tab(String name)
		{
			this.name = name;
		}
		
		public void updateSize()
		{
			width = 64;
			for(Feature feature : features)
			{
				int fWidth =
					Fonts.segoe18.getStringWidth(feature.getName()) + 10;
				if(fWidth > width)
					width = fWidth;
			}
			height = features.size() * 10;
		}
		
		public void onKeyPress(int keyCode)
		{
			switch(keyCode)
			{
				case Keyboard.KEY_DOWN:
				if(selected < features.size() - 1)
					selected++;
				else
					selected = 0;
				break;
				
				case Keyboard.KEY_UP:
				if(selected > 0)
					selected--;
				else
					selected = features.size() - 1;
				break;
				
				case Keyboard.KEY_RETURN:
				features.get(selected).doPrimaryAction();
				break;
			}
		}
		
		public void add(Feature feature)
		{
			features.add(feature);
		}
	}
}
