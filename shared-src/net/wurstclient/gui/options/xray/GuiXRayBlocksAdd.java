/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.gui.options.xray;

import java.io.IOException;

import org.lwjgl.input.Keyboard;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.wurstclient.features.mods.render.XRayMod;
import net.wurstclient.files.ConfigFiles;

public class GuiXRayBlocksAdd extends GuiScreen
{
	private GuiScreen prevScreen;
	
	private GuiTextField nameBox;
	private GuiButton addButton;
	
	public GuiXRayBlocksAdd(GuiScreen prevScreen)
	{
		this.prevScreen = prevScreen;
	}
	
	@Override
	public void updateScreen()
	{
		nameBox.updateCursorCounter();
		Block block = Block.getBlockFromName(nameBox.getText());
		addButton.enabled =
			!nameBox.getText().trim().isEmpty() && block != null;
	}
	
	@Override
	public void initGui()
	{
		Keyboard.enableRepeatEvents(true);
		
		buttonList.add(addButton =
			new GuiButton(0, width / 2 - 100, height / 4 + 120 + 12, "Add"));
		buttonList.add(
			new GuiButton(1, width / 2 - 100, height / 4 + 144 + 12, "Cancel"));
		
		nameBox =
			new GuiTextField(0, fontRendererObj, width / 2 - 100, 80, 200, 20);
		nameBox.setFocused(true);
	}
	
	@Override
	public void onGuiClosed()
	{
		Keyboard.enableRepeatEvents(false);
	}
	
	@Override
	protected void actionPerformed(GuiButton clickedButton)
	{
		if(!clickedButton.enabled)
			return;
		
		if(clickedButton.id == 0)
		{
			// Add
			Block block = Block.getBlockFromName(nameBox.getText());
			XRayMod.xrayBlocks.add(block);
			GuiXRayBlocksList.sortBlocks();
			ConfigFiles.XRAY.save();
			mc.displayGuiScreen(prevScreen);
			
		}else if(clickedButton.id == 1)
			mc.displayGuiScreen(prevScreen);
	}
	
	@Override
	protected void keyTyped(char par1, int par2)
	{
		nameBox.textboxKeyTyped(par1, par2);
		
		if(par2 == 28 || par2 == 156)
			actionPerformed(addButton);
	}
	
	@Override
	protected void mouseClicked(int par1, int par2, int par3) throws IOException
	{
		super.mouseClicked(par1, par2, par3);
		nameBox.mouseClicked(par1, par2, par3);
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		drawDefaultBackground();
		drawBackground(0);
		
		Block block = Block.getBlockFromName(nameBox.getText());
		int x = width / 2 - 9;
		int y = height / 2 - 32;
		ItemStack itemStack = new ItemStack(Item.getItemFromBlock(block));
		
		GlStateManager.enableRescaleNormal();
		GlStateManager.enableBlend();
		RenderHelper.enableGUIStandardItemLighting();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		
		if(itemStack.getItem() != null)
			try
			{
				mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, x, y);
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		else
			mc.fontRendererObj.drawString("?", x + 6, y + 5, 10526880);
		
		mc.getRenderItem().renderItemOverlays(mc.fontRendererObj, itemStack,
			x + 4, y + 4);
		
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableRescaleNormal();
		GlStateManager.disableBlend();
		
		try
		{
			drawCenteredString(fontRendererObj,
				"Name: " + (itemStack.getItem() == null
					? block.getLocalizedName() : itemStack.getDisplayName()),
				width / 2, y + 24, 10526880);
			drawCenteredString(fontRendererObj,
				"ID: " + Block.getIdFromBlock(block), width / 2, y + 36,
				10526880);
			drawCenteredString(fontRendererObj,
				"Block exists: " + (block != null), width / 2, y + 48,
				10526880);
			
		}catch(Exception e)
		{
			mc.fontRendererObj.drawString("?", x + 6, y + 5, 10526880);
			drawCenteredString(fontRendererObj, "Name: unknown", width / 2,
				y + 24, 10526880);
			drawCenteredString(fontRendererObj, "ID: unknown", width / 2,
				y + 36, 10526880);
			drawCenteredString(fontRendererObj,
				"Block exists: " + (block != null), width / 2, y + 48,
				10526880);
		}
		
		drawCenteredString(fontRendererObj, "Add X-Ray Block", width / 2, 20,
			16777215);
		drawString(fontRendererObj, "Name or ID", width / 2 - 100, 67,
			10526880);
		
		nameBox.drawTextBox();
		
		super.drawScreen(par1, par2, par3);
	}
}
