/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.commands;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import net.wurstclient.WurstClient;
import net.wurstclient.events.ChatOutputListener.ChatOutputEvent;
import net.wurstclient.events.GUIRenderListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Cmd;
import net.wurstclient.features.HelpPage;

@HelpPage("Commands/taco")
public final class TacoCmd extends Cmd
	implements GUIRenderListener, UpdateListener
{
	private final ResourceLocation[] tacos =
		{new ResourceLocation("wurst/dancingtaco1.png"),
			new ResourceLocation("wurst/dancingtaco2.png"),
			new ResourceLocation("wurst/dancingtaco3.png"),
			new ResourceLocation("wurst/dancingtaco4.png")};
	
	private boolean enabled;
	private int ticks = 0;
	
	public TacoCmd()
	{
		super("taco", "Spawns a dancing taco on your hotbar.\n"
			+ "\"I love that little guy. So cute!\" -WiZARD");
		setCategory(Category.FUN);
	}
	
	@Override
	public void call(String[] args) throws CmdException
	{
		if(args.length != 0)
			throw new CmdSyntaxError("Tacos don't need arguments!");
		
		enabled = !enabled;
		if(enabled)
		{
			wurst.events.add(GUIRenderListener.class, this);
			wurst.events.add(UpdateListener.class, this);
		}else
		{
			wurst.events.remove(GUIRenderListener.class, this);
			wurst.events.remove(UpdateListener.class, this);
		}
	}
	
	@Override
	public String getPrimaryAction()
	{
		return "Be a BOSS!";
	}
	
	@Override
	public void doPrimaryAction()
	{
		wurst.commands.onSentMessage(new ChatOutputEvent(".taco", true));
	}
	
	@Override
	public void onRenderGUI()
	{
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		if(WurstClient.INSTANCE.mods.rainbowUiMod.isActive())
		{
			float[] acColor = WurstClient.INSTANCE.getGui().getAcColor();
			GL11.glColor4f(acColor[0], acColor[1], acColor[2], 1);
		}else
			GL11.glColor4f(1, 1, 1, 1);
		
		mc.getTextureManager().bindTexture(tacos[ticks / 8]);
		ScaledResolution sr = new ScaledResolution(mc);
		int x = sr.getScaledWidth() / 2 - 32 + 76;
		int y = sr.getScaledHeight() - 32 - 19;
		int w = 64;
		int h = 32;
		Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, w, h, w, h);
		
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	@Override
	public void onUpdate()
	{
		if(ticks >= 31)
			ticks = 0;
		else
			ticks++;
	}
}
