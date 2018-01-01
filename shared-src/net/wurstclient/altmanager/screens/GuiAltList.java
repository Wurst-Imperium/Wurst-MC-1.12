/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.altmanager.screens;

import java.util.ArrayList;
import java.util.Collections;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiSlot;
import net.wurstclient.altmanager.Alt;
import net.wurstclient.altmanager.AltRenderer;
import net.wurstclient.compatibility.WMath;

public final class GuiAltList extends GuiSlot
{
	public GuiAltList(Minecraft mc, AltManagerScreen prevScreen)
	{
		super(mc, prevScreen.width, prevScreen.height, 36,
			prevScreen.height - 56, 30);
		
		this.prevScreen = prevScreen;
	}
	
	private int selectedSlot;
	
	public static ArrayList<Alt> alts = new ArrayList<>();
	public static int premiumAlts;
	public static int crackedAlts;
	
	private AltManagerScreen prevScreen;
	
	public static void sortAlts()
	{
		// totally overcomplicated sorting algorithm
		
		Collections.sort(alts, (o1, o2) -> {
			
			if(o1 == null || o2 == null)
				return 0;
			
			return o1.getNameOrEmail().compareToIgnoreCase(o2.getNameOrEmail());
		});
		
		ArrayList<Alt> newAlts = new ArrayList<>();
		premiumAlts = 0;
		crackedAlts = 0;
		
		for(int i = 0; i < alts.size(); i++)
			if(alts.get(i).isStarred())
				newAlts.add(alts.get(i));
			
		for(int i = 0; i < alts.size(); i++)
			if(!alts.get(i).isCracked() && !alts.get(i).isStarred())
				newAlts.add(alts.get(i));
			
		for(int i = 0; i < alts.size(); i++)
			if(alts.get(i).isCracked() && !alts.get(i).isStarred())
				newAlts.add(alts.get(i));
			
		for(int i = 0; i < newAlts.size(); i++)
			for(int i2 = 0; i2 < newAlts.size(); i2++)
				if(i != i2
					&& newAlts.get(i).getEmail()
						.equals(newAlts.get(i2).getEmail())
					&& newAlts.get(i).isCracked() == newAlts.get(i2)
						.isCracked())
					newAlts.remove(i2);
				
		for(int i = 0; i < newAlts.size(); i++)
			if(newAlts.get(i).isCracked())
				crackedAlts++;
			else
				premiumAlts++;
			
		alts = newAlts;
	}
	
	@Override
	protected boolean isSelected(int id)
	{
		return selectedSlot == id;
	}
	
	protected int getSelectedSlot()
	{
		return WMath.clamp(selectedSlot, -1, alts.size() - 1);
	}
	
	protected Alt getSelectedAlt()
	{
		if(alts.isEmpty())
			return null;
		
		return alts.get(getSelectedSlot());
	}
	
	protected void removeSelectedAlt()
	{
		if(alts.isEmpty())
			return;
		
		alts.remove(getSelectedSlot());
	}
	
	@Override
	protected int getSize()
	{
		return alts.size();
	}
	
	@Override
	protected void elementClicked(int var1, boolean doubleClick, int var3,
		int var4)
	{
		selectedSlot = var1;
		
		if(doubleClick)
			prevScreen.actionPerformed(new GuiButton(0, 0, 0, null));
	}
	
	@Override
	protected void drawBackground()
	{
		
	}
	
	@Override
	protected void drawSlot(int id, int x, int y, int var4, int var5, int var6,
		float partialTicks)
	{
		Alt alt = alts.get(id);
		
		// green glow when logged in
		if(mc.getSession().getUsername().equals(alt.getName()))
		{
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glEnable(GL11.GL_BLEND);
			
			float opacity = 0.3F - Math.abs(WMath.sin(Minecraft.getSystemTime()
				% 10000L / 10000F * (float)Math.PI * 2.0F) * 0.15F);
			
			GL11.glColor4f(0, 1, 0, opacity);
			
			GL11.glBegin(GL11.GL_QUADS);
			{
				GL11.glVertex2d(x - 2, y - 2);
				GL11.glVertex2d(x - 2 + 220, y - 2);
				GL11.glVertex2d(x - 2 + 220, y - 2 + 30);
				GL11.glVertex2d(x - 2, y - 2 + 30);
			}
			GL11.glEnd();
			
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glDisable(GL11.GL_BLEND);
		}
		
		// face
		AltRenderer.drawAltFace(alt.getNameOrEmail(), x + 1, y + 1, 24, 24,
			isSelected(id));
		
		// name / email
		mc.fontRendererObj.drawString("Name: " + alt.getNameOrEmail(), x + 31,
			y + 3, 10526880);
		
		// tags
		String tags = alt.isCracked() ? "§8cracked" : "§2premium";
		if(alt.isStarred())
			tags += "§r, §estarred";
		if(alt.isUnchecked())
			tags += "§r, §cunchecked";
		mc.fontRendererObj.drawString(tags, x + 31, y + 15, 10526880);
	}
}
