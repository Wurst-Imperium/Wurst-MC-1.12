/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.items;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.features.Category;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;
import net.wurstclient.utils.ChatUtils;
import net.wurstclient.utils.InventoryUtils;

@SearchTags({"CmdBlock", "CommandBlock", "cmd block", "command block"})
@Mod.Bypasses
public final class CmdBlockMod extends Mod
{
	public CmdBlockMod()
	{
		super("CMD-Block",
			"Allows you to make a Command Block without having OP.\n"
				+ "Requires creative mode.\n"
				+ "Appears to be patched on Spigot.");
		setCategory(Category.ITEMS);
	}
	
	@Override
	public void onEnable()
	{
		// check gamemode
		if(!WMinecraft.getPlayer().capabilities.isCreativeMode)
		{
			ChatUtils.error("Creative mode only.");
			setEnabled(false);
			return;
		}
		
		// show cmd-block screen
		mc.displayGuiScreen(new GuiCmdBlock(mc.currentScreen));
		setEnabled(false);
	}
	
	public void createCmdBlock(String cmd)
	{
		// generate cmd-block
		ItemStack stack = new ItemStack(Blocks.COMMAND_BLOCK);
		NBTTagCompound nbtTagCompound = new NBTTagCompound();
		nbtTagCompound.setTag("Command", new NBTTagString(cmd));
		stack.writeToNBT(nbtTagCompound);
		stack.setTagInfo("BlockEntityTag", nbtTagCompound);
		
		// give cmd-block
		if(InventoryUtils.placeStackInHotbar(stack))
			ChatUtils.message("Command Block created.");
		else
			ChatUtils.error("Please clear a slot in your hotbar.");
	}
	
	private class GuiCmdBlock extends GuiScreen
	{
		private GuiScreen prevScreen;
		private GuiTextField commandBox;
		
		public GuiCmdBlock(GuiScreen prevScreen)
		{
			this.prevScreen = prevScreen;
		}
		
		@Override
		public void initGui()
		{
			buttonList.add(new GuiButton(0, width / 2 - 100, height / 3 * 2,
				200, 20, "Done"));
			buttonList.add(new GuiButton(1, width / 2 - 100,
				height / 3 * 2 + 24, 200, 20, "Cancel"));
			
			commandBox = new GuiTextField(0, fontRendererObj, width / 2 - 100,
				60, 200, 20);
			commandBox.setMaxStringLength(100);
			commandBox.setFocused(true);
			commandBox.setText("/");
		}
		
		@Override
		protected void actionPerformed(GuiButton button) throws IOException
		{
			if(!button.enabled)
				return;
			
			switch(button.id)
			{
				case 0:
				Minecraft.getMinecraft().displayGuiScreen(prevScreen);
				createCmdBlock(commandBox.getText());
				break;
				
				case 1:
				Minecraft.getMinecraft().displayGuiScreen(prevScreen);
				break;
			}
		}
		
		@Override
		public void updateScreen()
		{
			commandBox.updateCursorCounter();
		}
		
		@Override
		protected void keyTyped(char typedChar, int keyCode)
		{
			commandBox.textboxKeyTyped(typedChar, keyCode);
		}
		
		@Override
		protected void mouseClicked(int x, int y, int button) throws IOException
		{
			super.mouseClicked(x, y, button);
			commandBox.mouseClicked(x, y, button);
		}
		
		@Override
		public void drawScreen(int mouseX, int mouseY, float partialTicks)
		{
			drawDefaultBackground();
			drawCenteredString(fontRendererObj, "CMD-Block", width / 2, 20,
				0xffffff);
			
			drawString(fontRendererObj, "Command", width / 2 - 100, 47,
				0xa0a0a0);
			drawCenteredString(fontRendererObj,
				"The command you type in here will be", width / 2, 100,
				0xa0a0a0);
			drawCenteredString(fontRendererObj,
				"executed by the Command Block.", width / 2, 110, 0xa0a0a0);
			
			commandBox.drawTextBox();
			super.drawScreen(mouseX, mouseY, partialTicks);
		}
	}
}
