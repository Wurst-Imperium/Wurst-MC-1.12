/*
 * Copyright © 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.clickgui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.FontRenderer;
import net.wurstclient.WurstClient;
import net.wurstclient.features.Feature;
import net.wurstclient.font.Fonts;

public final class FeatureButton extends Component
{
	private final Feature feature;
	
	public FeatureButton(Feature feature)
	{
		this.feature = feature;
		setWidth(getDefaultWidth());
		setHeight(getDefaultHeight());
	}
	
	@Override
	public void handleMouseClick(int mouseX, int mouseY, int mouseButton)
	{
		if(mouseButton != 0)
			return;
		
		feature.doPrimaryAction();
	}
	
	@Override
	public void render(int mouseX, int mouseY)
	{
		ClickGui gui = WurstClient.INSTANCE.getGui();
		float[] bgColor = gui.getBgColor();
		float[] acColor = gui.getAcColor();
		
		int x1 = getX();
		int y1 = getY();
		int x2 = x1 + getWidth();
		int y2 = y1 + getHeight();
		
		int scroll = getParent().isScrollingEnabled()
			? getParent().getScrollOffset() : 0;
		boolean hovering = mouseX >= x1 && mouseY >= y1 && mouseX < x2
			&& mouseY < y2 && mouseY >= -scroll
			&& mouseY < getParent().getHeight() - 13 - scroll;
		
		// tooltip
		if(hovering)
			gui.setTooltip(feature.getDescription());
		
		// color
		if(feature.isEnabled())
			if(feature.isBlocked())
				GL11.glColor4f(1, 0, 0, hovering ? 0.75F : 0.5F);
			else
				GL11.glColor4f(0, 1, 0, hovering ? 0.75F : 0.5F);
		else
			GL11.glColor4f(bgColor[0], bgColor[1], bgColor[2],
				hovering ? 0.75F : 0.5F);
		
		// background
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex2i(x1, y1);
		GL11.glVertex2i(x1, y2);
		GL11.glVertex2i(x2, y2);
		GL11.glVertex2i(x2, y1);
		GL11.glEnd();
		
		// outline
		GL11.glColor4f(acColor[0], acColor[1], acColor[2], 0.5F);
		GL11.glBegin(GL11.GL_LINE_LOOP);
		GL11.glVertex2i(x1, y1);
		GL11.glVertex2i(x1, y2);
		GL11.glVertex2i(x2, y2);
		GL11.glVertex2i(x2, y1);
		GL11.glEnd();
		
		// hack name
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		FontRenderer fr = Fonts.segoe18;
		int fx = x1 + (getWidth() - fr.getStringWidth(feature.getName())) / 2;
		int fy = y1 - 1;
		fr.drawString(feature.getName(), fx, fy, 0xf0f0f0);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
	}
	
	@Override
	public int getDefaultWidth()
	{
		return Fonts.segoe18.getStringWidth(feature.getName()) + 2;
	}
	
	@Override
	public int getDefaultHeight()
	{
		return 11;
	}
}
