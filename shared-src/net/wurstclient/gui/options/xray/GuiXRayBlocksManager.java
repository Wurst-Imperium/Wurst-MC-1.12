/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.gui.options.xray;

import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.wurstclient.features.mods.render.XRayMod;
import net.wurstclient.files.ConfigFiles;

public class GuiXRayBlocksManager extends GuiScreen
{
	private GuiScreen prevScreen;
	private GuiXRayBlocksList blockList;
	
	private GuiButton addButton;
	private GuiButton removeButton;
	
	public GuiXRayBlocksManager(GuiScreen prevScreen)
	{
		this.prevScreen = prevScreen;
	}
	
	@Override
	public void initGui()
	{
		blockList = new GuiXRayBlocksList(mc, this);
		blockList.registerScrollButtons(7, 8);
		GuiXRayBlocksList.sortBlocks();
		blockList.elementClicked(-1, false, 0, 0);
		
		buttonList.add(addButton =
			new GuiButton(0, width / 2 - 100, height - 52, 98, 20, "Add"));
		buttonList.add(removeButton =
			new GuiButton(1, width / 2 + 2, height - 52, 98, 20, "Remove"));
		buttonList.add(
			new GuiButton(2, width / 2 - 100, height - 28, 200, 20, "Back"));
	}
	
	@Override
	public void updateScreen()
	{
		removeButton.enabled =
			blockList.getSelectedSlot() != -1 && !XRayMod.xrayBlocks.isEmpty();
	}
	
	@Override
	protected void actionPerformed(GuiButton button)
	{
		if(!button.enabled)
			return;
		
		if(button.id == 0)
			mc.displayGuiScreen(new GuiXRayBlocksAdd(this));
		else if(button.id == 1)
		{
			// remove
			XRayMod.xrayBlocks.remove(blockList.getSelectedSlot());
			GuiXRayBlocksList.sortBlocks();
			ConfigFiles.XRAY.save();
			
		}else if(button.id == 2)
			mc.displayGuiScreen(prevScreen);
	}
	
	@Override
	protected void keyTyped(char par1, int par2)
	{
		if(par2 == 28 || par2 == 156)
			actionPerformed(addButton);
	}
	
	@Override
	protected void mouseClicked(int par1, int par2, int par3) throws IOException
	{
		if(par2 >= 36 && par2 <= height - 57)
			if(par1 >= width / 2 + 140 || par1 <= width / 2 - 126)
				blockList.elementClicked(-1, false, 0, 0);
			
		super.mouseClicked(par1, par2, par3);
	}
	
	@Override
	public void handleMouseInput() throws IOException
	{
		super.handleMouseInput();
		blockList.handleMouseInput();
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		drawDefaultBackground();
		
		blockList.drawScreen(par1, par2, par3);
		
		drawCenteredString(fontRendererObj, "X-Ray Block Manager", width / 2, 8,
			16777215);
		drawCenteredString(fontRendererObj,
			"Blocks: " + XRayMod.xrayBlocks.size(), width / 2, 20, 16777215);
		
		super.drawScreen(par1, par2, par3);
	}
}
