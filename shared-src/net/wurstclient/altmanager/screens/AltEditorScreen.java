/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.altmanager.screens;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.wurstclient.altmanager.AltRenderer;
import net.wurstclient.altmanager.NameGenerator;
import net.wurstclient.altmanager.PasswordField;
import net.wurstclient.files.WurstFolders;
import net.wurstclient.utils.MiscUtils;

public abstract class AltEditorScreen extends GuiScreen
{
	protected final GuiScreen prevScreen;
	
	private GuiTextField emailBox;
	private PasswordField passwordBox;
	
	private GuiButton doneButton;
	private GuiButton stealSkinButton;
	
	protected String message = "";
	private int errorTimer;
	
	public AltEditorScreen(GuiScreen prevScreen)
	{
		this.prevScreen = prevScreen;
	}
	
	@Override
	public final void updateScreen()
	{
		emailBox.updateCursorCounter();
		passwordBox.updateCursorCounter();
		
		String email = emailBox.getText().trim();
		boolean alex = email.equalsIgnoreCase("Alexander01998");
		
		doneButton.enabled =
			!email.isEmpty() && !(alex && passwordBox.getText().isEmpty());
		
		stealSkinButton.enabled = !alex;
	}
	
	protected abstract String getTitle();
	
	protected final String getEmail()
	{
		return emailBox.getText();
	}
	
	protected final String getPassword()
	{
		return passwordBox.getText();
	}
	
	protected String getDefaultEmail()
	{
		return mc.session.getUsername();
	}
	
	protected String getDefaultPassword()
	{
		return "";
	}
	
	protected abstract String getDoneButtonText();
	
	protected abstract void pressDoneButton();
	
	protected final void doErrorEffect()
	{
		errorTimer = 8;
	}
	
	@Override
	public final void initGui()
	{
		Keyboard.enableRepeatEvents(true);
		
		buttonList.add(doneButton = new GuiButton(0, width / 2 - 100,
			height / 4 + 72 + 12, getDoneButtonText()));
		buttonList.add(
			new GuiButton(1, width / 2 - 100, height / 4 + 120 + 12, "Cancel"));
		buttonList.add(new GuiButton(3, width / 2 - 100, height / 4 + 96 + 12,
			"Random Name"));
		buttonList.add(stealSkinButton =
			new GuiButton(4, width - (width / 2 - 100) / 2 - 64, height - 32,
				128, 20, "Steal Skin"));
		buttonList.add(new GuiButton(5, (width / 2 - 100) / 2 - 64, height - 32,
			128, 20, "Open Skin Folder"));
		
		emailBox =
			new GuiTextField(0, fontRendererObj, width / 2 - 100, 60, 200, 20);
		emailBox.setMaxStringLength(48);
		emailBox.setFocused(true);
		emailBox.setText(getDefaultEmail());
		
		passwordBox =
			new PasswordField(fontRendererObj, width / 2 - 100, 100, 200, 20);
		passwordBox.setFocused(false);
		passwordBox.setText(getDefaultPassword());
	}
	
	@Override
	public final void onGuiClosed()
	{
		Keyboard.enableRepeatEvents(false);
	}
	
	@Override
	protected final void actionPerformed(GuiButton button)
	{
		if(!button.enabled)
			return;
		
		switch(button.id)
		{
			case 0:
			pressDoneButton();
			break;
			
			case 1:
			mc.displayGuiScreen(prevScreen);
			break;
			
			case 3:
			emailBox.setText(NameGenerator.generateName());
			break;
			
			case 4:
			message = stealSkin(getEmail());
			break;
			
			case 5:
			MiscUtils.openFile(WurstFolders.SKINS);
			break;
		}
	}
	
	private final String stealSkin(String name)
	{
		String skin = name + ".png";
		
		URI u = URI.create("http://skins.minecraft.net/MinecraftSkins/")
			.resolve(skin);
		Path path = WurstFolders.SKINS.resolve(skin);
		
		try(InputStream in = u.toURL().openStream())
		{
			Files.copy(in, path);
			return "§a§lSaved skin as " + skin;
			
		}catch(IOException e)
		{
			e.printStackTrace();
			return "§4§lSkin could not be saved.";
		}
	}
	
	@Override
	protected final void keyTyped(char par1, int par2)
	{
		emailBox.textboxKeyTyped(par1, par2);
		passwordBox.textboxKeyTyped(par1, par2);
		
		if(par2 == 28 || par2 == 156)
			actionPerformed(doneButton);
	}
	
	@Override
	protected final void mouseClicked(int par1, int par2, int par3)
		throws IOException
	{
		super.mouseClicked(par1, par2, par3);
		
		emailBox.mouseClicked(par1, par2, par3);
		passwordBox.mouseClicked(par1, par2, par3);
		
		if(emailBox.isFocused() || passwordBox.isFocused())
			message = "";
	}
	
	@Override
	public final void drawScreen(int par1, int par2, float par3)
	{
		drawDefaultBackground();
		
		// skin preview
		AltRenderer.drawAltBack(emailBox.getText(), (width / 2 - 100) / 2 - 64,
			height / 2 - 128, 128, 256);
		AltRenderer.drawAltBody(emailBox.getText(),
			width - (width / 2 - 100) / 2 - 64, height / 2 - 128, 128, 256);
		
		// text
		drawCenteredString(fontRendererObj, getTitle(), width / 2, 20,
			16777215);
		drawString(fontRendererObj, "Name or E-Mail", width / 2 - 100, 47,
			10526880);
		drawString(fontRendererObj, "Password", width / 2 - 100, 87, 10526880);
		drawCenteredString(fontRendererObj, message, width / 2, 142, 16777215);
		
		// text boxes
		emailBox.drawTextBox();
		passwordBox.drawTextBox();
		
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
