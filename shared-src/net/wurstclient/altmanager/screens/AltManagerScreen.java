/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.altmanager.screens;

import java.awt.Component;
import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNo;
import net.wurstclient.altmanager.Alt;
import net.wurstclient.altmanager.AltRenderer;
import net.wurstclient.altmanager.LoginManager;
import net.wurstclient.altmanager.NameGenerator;
import net.wurstclient.files.ConfigFiles;
import net.wurstclient.files.WurstFolders;
import net.wurstclient.hooks.FrameHook;
import net.wurstclient.utils.MiscUtils;

public final class AltManagerScreen extends GuiScreen
{
	public static GuiAltList altList;
	
	private GuiScreen prevScreen;
	private boolean shouldAsk = true;
	private int errorTimer;
	
	public AltManagerScreen(GuiScreen par1GuiScreen)
	{
		prevScreen = par1GuiScreen;
	}
	
	@Override
	public void initGui()
	{
		altList = new GuiAltList(mc, this);
		altList.registerScrollButtons(7, 8);
		altList.elementClicked(-1, false, 0, 0);
		
		if(GuiAltList.alts.isEmpty() && shouldAsk)
			mc.displayGuiScreen(new GuiYesNo(this, "Your alt list is empty.",
				"Would you like some random alts to get started?", 0));
		
		buttonList.clear();
		buttonList.add(
			new GuiButton(0, width / 2 - 154, height - 52, 100, 20, "Use"));
		buttonList.add(new GuiButton(1, width / 2 - 50, height - 52, 100, 20,
			"Direct Login"));
		buttonList
			.add(new GuiButton(2, width / 2 + 54, height - 52, 100, 20, "Add"));
		buttonList.add(
			new GuiButton(3, width / 2 - 154, height - 28, 75, 20, "Star"));
		buttonList
			.add(new GuiButton(4, width / 2 - 76, height - 28, 74, 20, "Edit"));
		buttonList.add(
			new GuiButton(5, width / 2 + 2, height - 28, 74, 20, "Delete"));
		buttonList.add(
			new GuiButton(6, width / 2 + 80, height - 28, 75, 20, "Cancel"));
		buttonList.add(new GuiButton(7, 8, 8, 100, 20, "Import Alts"));
	}
	
	@Override
	public void updateScreen()
	{
		buttonList.get(0).enabled =
			!GuiAltList.alts.isEmpty() && altList.getSelectedSlot() != -1;
		buttonList.get(3).enabled =
			!GuiAltList.alts.isEmpty() && altList.getSelectedSlot() != -1;
		buttonList.get(4).enabled =
			!GuiAltList.alts.isEmpty() && altList.getSelectedSlot() != -1;
		buttonList.get(5).enabled =
			!GuiAltList.alts.isEmpty() && altList.getSelectedSlot() != -1;
	}
	
	@Override
	public void actionPerformed(GuiButton button)
	{
		if(!button.enabled)
			return;
		
		if(button.id == 0)
		{
			// "Use" button
			Alt alt = altList.getSelectedAlt();
			
			if(alt.isCracked())
			{
				LoginManager.changeCrackedName(alt.getEmail());
				mc.displayGuiScreen(prevScreen);
				
			}else
			{
				String reply =
					LoginManager.login(alt.getEmail(), alt.getPassword());
				
				if(reply.isEmpty())
				{
					mc.displayGuiScreen(prevScreen);
					alt.setChecked(mc.session.getUsername());
					ConfigFiles.ALTS.save();
					
				}else
					errorTimer = 8;
			}
		}else if(button.id == 1)
			// "Direct Login" button
			mc.displayGuiScreen(new DirectLoginScreen(this));
		else if(button.id == 2)
			// "Add" button
			mc.displayGuiScreen(new AddAltScreen(this));
		else if(button.id == 3)
		{
			// "Star" button
			Alt alt = altList.getSelectedAlt();
			alt.setStarred(!alt.isStarred());
			GuiAltList.sortAlts();
			ConfigFiles.ALTS.save();
			
		}else if(button.id == 4)
		{
			// "Edit" button
			Alt alt = altList.getSelectedAlt();
			mc.displayGuiScreen(new EditAltScreen(this, alt));
			
		}else if(button.id == 5)
			// "Delete" button
			mc.displayGuiScreen(
				new GuiYesNo(this, "Are you sure you want to remove this alt?",
					"\"" + altList.getSelectedAlt().getNameOrEmail()
						+ "\" will be lost forever! (A long time!)",
					"Delete", "Cancel", 1));
		else if(button.id == 6)
			// "Cancel" button
			mc.displayGuiScreen(prevScreen);
		else if(button.id == 7)
			// "Import Alts" button
			new Thread(() -> {
				JFileChooser fileChooser =
					new JFileChooser(WurstFolders.MAIN.toFile())
					{
						@Override
						protected JDialog createDialog(Component parent)
							throws HeadlessException
						{
							JDialog dialog = super.createDialog(parent);
							dialog.setAlwaysOnTop(true);
							return dialog;
						}
					};
					
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fileChooser.setAcceptAllFileFilterUsed(false);
				fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(
					"TXT file (username:password)", "txt"));
				
				if(fileChooser.showOpenDialog(
					FrameHook.getFrame()) == JFileChooser.APPROVE_OPTION)
					try
					{
						File file = fileChooser.getSelectedFile();
						BufferedReader load =
							new BufferedReader(new FileReader(file));
						
						for(String line = ""; (line = load.readLine()) != null;)
						{
							String[] data = line.split(":");
							if(data.length != 2)
								continue;
							GuiAltList.alts
								.add(new Alt(data[0], data[1], null));
						}
						
						load.close();
						GuiAltList.sortAlts();
						ConfigFiles.ALTS.save();
						
					}catch(IOException e)
					{
						e.printStackTrace();
						MiscUtils.simpleError(e, fileChooser);
					}
			}).start();
	}
	
	@Override
	public void confirmClicked(boolean par1, int par2)
	{
		if(par2 == 0)
		{
			if(par1)
			{
				for(int i = 0; i < 8; i++)
					GuiAltList.alts
						.add(new Alt(NameGenerator.generateName(), null, null));
				
				GuiAltList.sortAlts();
				ConfigFiles.ALTS.save();
			}
			
			shouldAsk = false;
			
		}else if(par2 == 1)
			if(par1)
			{
				altList.removeSelectedAlt();
				GuiAltList.sortAlts();
				ConfigFiles.ALTS.save();
			}
		
		mc.displayGuiScreen(this);
	}
	
	@Override
	protected void keyTyped(char par1, int par2)
	{
		if(par2 == 28 || par2 == 156)
			actionPerformed(buttonList.get(0));
	}
	
	@Override
	protected void mouseClicked(int par1, int par2, int par3) throws IOException
	{
		if(par2 >= 36 && par2 <= height - 57)
			if(par1 >= width / 2 + 140 || par1 <= width / 2 - 126)
				altList.elementClicked(-1, false, 0, 0);
			
		super.mouseClicked(par1, par2, par3);
	}
	
	@Override
	public void handleMouseInput() throws IOException
	{
		super.handleMouseInput();
		altList.handleMouseInput();
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		drawDefaultBackground();
		altList.drawScreen(par1, par2, par3);
		
		// skin preview
		if(altList.getSelectedSlot() != -1
			&& altList.getSelectedSlot() < GuiAltList.alts.size())
		{
			Alt alt = altList.getSelectedAlt();
			AltRenderer.drawAltBack(alt.getNameOrEmail(),
				(width / 2 - 125) / 2 - 32, height / 2 - 64 - 9, 64, 128);
			AltRenderer.drawAltBody(alt.getNameOrEmail(),
				width - (width / 2 - 140) / 2 - 32, height / 2 - 64 - 9, 64,
				128);
		}
		
		// title text
		drawCenteredString(fontRendererObj, "Alt Manager", width / 2, 4,
			16777215);
		drawCenteredString(fontRendererObj, "Alts: " + GuiAltList.alts.size(),
			width / 2, 14, 10526880);
		drawCenteredString(
			fontRendererObj, "premium: " + GuiAltList.premiumAlts
				+ ", cracked: " + GuiAltList.crackedAlts,
			width / 2, 24, 10526880);
		
		// red flash for errors
		if(errorTimer > 0)
		{
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glEnable(GL11.GL_BLEND);
			
			GL11.glColor4f(1, 0, 0, errorTimer / 16F);
			
			GL11.glBegin(GL11.GL_QUADS);
			{
				GL11.glVertex2d(0, 0);
				GL11.glVertex2d(width, 0);
				GL11.glVertex2d(width, height);
				GL11.glVertex2d(0, height);
			}
			GL11.glEnd();
			
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glDisable(GL11.GL_BLEND);
			errorTimer--;
		}
		
		super.drawScreen(par1, par2, par3);
	}
}
