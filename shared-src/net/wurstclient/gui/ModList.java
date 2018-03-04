/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.gui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.wurstclient.WurstClient;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Mod;
import net.wurstclient.features.mods.other.NavigatorMod;
import net.wurstclient.features.special_features.ModListSpf;
import net.wurstclient.features.special_features.YesCheatSpf;
import net.wurstclient.font.Fonts;

public final class ModList implements UpdateListener
{
	private final ArrayList<Entry> activeMods = new ArrayList<>();
	private final ModListSpf modListSpf =
		WurstClient.INSTANCE.special.modListSpf;
	private int posY;
	private ScaledResolution sr;
	private int textColor;
	
	public ModList()
	{
		for(Mod mod : WurstClient.INSTANCE.mods.getAllMods())
		{
			if(mod instanceof NavigatorMod)
				continue;
			
			if(mod.isActive())
				activeMods.add(new Entry(mod, 0));
		}
		
		WurstClient.INSTANCE.events.add(UpdateListener.class, this);
	}
	
	public void render(float partialTicks)
	{
		if(modListSpf.isHidden())
			return;
		
		if(modListSpf.isPositionRight())
			posY = 0;
		else
			posY = 19;
		sr = new ScaledResolution(Minecraft.getMinecraft());
		
		// color
		if(WurstClient.INSTANCE.mods.rainbowUiMod.isActive())
		{
			float[] acColor = WurstClient.INSTANCE.getGui().getAcColor();
			textColor = 0x04 << 24 | (int)(acColor[0] * 256) << 16
				| (int)(acColor[1] * 256) << 8 | (int)(acColor[2] * 256);
		}else
			textColor = 0x04ffffff;
		
		// YesCheat+ mode indicator
		YesCheatSpf yesCheatSpf = WurstClient.INSTANCE.special.yesCheatSpf;
		if(yesCheatSpf.modeIndicator.isChecked())
			drawString("YesCheat+: " + yesCheatSpf.getProfile().getName());
		
		int height = posY + activeMods.size() * 9;
		if(modListSpf.isCountMode() || height > sr.getScaledHeight())
			
			// draw counter
			drawString(activeMods.size() == 1 ? "1 mod active"
				: activeMods.size() + " mods active");
		
		// draw mod list
		else if(modListSpf.isAnimations())
			for(Entry e : activeMods)
				drawWithOffset(e, partialTicks);
		else
			for(Entry e : activeMods)
				drawString(e.mod.getRenderName());
	}
	
	public void updateState(Mod mod)
	{
		if(mod.isActive())
		{
			for(Entry e : activeMods)
				if(e.mod == mod)
					return;
				
			activeMods.add(new Entry(mod, 4));
			activeMods.sort(Comparator.comparing(e -> e.mod.getName()));
			
		}else if(!modListSpf.isAnimations())
			activeMods.removeIf(e -> e.mod == mod);
	}
	
	@Override
	public void onUpdate()
	{
		if(!modListSpf.isAnimations())
			return;
		
		for(Iterator<Entry> itr = activeMods.iterator(); itr.hasNext();)
		{
			Entry e = itr.next();
			
			if(e.mod.isActive())
			{
				e.prevOffset = e.offset;
				if(e.offset > 0)
					e.offset--;
				
			}else if(!e.mod.isActive() && e.offset < 4)
			{
				e.prevOffset = e.offset;
				e.offset++;
				
			}else if(!e.mod.isActive() && e.offset == 4)
				itr.remove();
		}
	}
	
	private void drawString(String s)
	{
		int posX;
		if(modListSpf.isPositionRight())
			posX = sr.getScaledWidth() - Fonts.segoe18.getStringWidth(s) - 2;
		else
			posX = 2;
		
		Fonts.segoe18.drawString(s, posX + 1, posY + 1, 0xff000000);
		Fonts.segoe18.drawString(s, posX, posY, textColor | 0xff000000);
		
		posY += 9;
	}
	
	private void drawWithOffset(Entry e, float partialTicks)
	{
		String s = e.mod.getRenderName();
		float offset =
			e.offset * partialTicks + e.prevOffset * (1 - partialTicks);
		
		float posX;
		if(modListSpf.isPositionRight())
			posX = sr.getScaledWidth() - Fonts.segoe18.getStringWidth(s) - 2
				+ 5 * offset;
		else
			posX = 2 - 5 * offset;
		
		int alpha = (int)(255 * (1 - offset / 4)) << 24;
		Fonts.segoe18.drawString(s, posX + 1, posY + 1, 0x04000000 | alpha);
		Fonts.segoe18.drawString(s, posX, posY, textColor | alpha);
		
		posY += 9;
	}
	
	private static final class Entry
	{
		private final Mod mod;
		private int offset;
		private int prevOffset;
		
		public Entry(Mod mod, int offset)
		{
			this.mod = mod;
			this.offset = offset;
			prevOffset = offset;
		}
	}
}
