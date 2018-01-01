/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.hooks;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Frame;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.util.ResourceLocation;
import net.wurstclient.bot.WurstBot;
import net.wurstclient.compatibility.WMinecraft;

public class FrameHook
{
	private static JFrame frame;
	
	public static void createFrame(DefaultResourcePack mcDefaultResourcePack,
		Logger logger) throws LWJGLException
	{
		// check if frame should be created
		if(!isAutoMaximize() && !WurstBot.isEnabled())
			return;
		
		// create frame
		frame = new JFrame("Minecraft " + WMinecraft.DISPLAY_VERSION);
		
		// add LWJGL
		Canvas canvas = new Canvas();
		canvas.setBackground(new Color(16, 16, 16));
		Display.setParent(canvas);
		Minecraft mc = Minecraft.getMinecraft();
		canvas.setSize(mc.displayWidth, mc.displayHeight);
		frame.add(canvas);
		
		// configure frame
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setLocationRelativeTo(null);
		
		// add icons
		InputStream icon16 = null;
		InputStream icon32 = null;
		try
		{
			icon16 = mcDefaultResourcePack.getInputStreamAssets(
				new ResourceLocation("icons/icon_16x16.png"));
			icon32 = mcDefaultResourcePack.getInputStreamAssets(
				new ResourceLocation("icons/icon_32x32.png"));
			ArrayList<BufferedImage> icons = new ArrayList<>();
			icons.add(ImageIO.read(icon16));
			icons.add(ImageIO.read(icon32));
			frame.setIconImages(icons);
		}catch(Exception e)
		{
			logger.error("Couldn't set icon", e);
		}finally
		{
			IOUtils.closeQuietly(icon16);
			IOUtils.closeQuietly(icon32);
		}
		
		// show frame
		if(!WurstBot.isEnabled())
			frame.setVisible(true);
	}
	
	private static boolean isAutoMaximize()
	{
		File autoMaximizeFile = new File(
			Minecraft.getMinecraft().mcDataDir + "/wurst/automaximize.json");
		boolean autoMaximizeEnabled = false;
		if(!autoMaximizeFile.exists())
			createAutoMaximizeFile(autoMaximizeFile);
		try
		{
			BufferedReader load =
				new BufferedReader(new FileReader(autoMaximizeFile));
			String line = load.readLine();
			load.close();
			Minecraft.getMinecraft();
			autoMaximizeEnabled = line.equals("true")
				&& !WMinecraft.isRunningOnMac() && !WMinecraft.OPTIFINE;
		}catch(IOException e)
		{
			e.printStackTrace();
		}
		return autoMaximizeEnabled;
	}
	
	private static void createAutoMaximizeFile(File autoMaximizeFile)
	{
		try
		{
			if(!autoMaximizeFile.getParentFile().exists())
				autoMaximizeFile.getParentFile().mkdirs();
			PrintWriter save =
				new PrintWriter(new FileWriter(autoMaximizeFile));
			save.println(Boolean.toString(
				!WMinecraft.isRunningOnMac() && !WMinecraft.OPTIFINE));
			save.close();
		}catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void maximize()
	{
		if(frame != null)
			frame.setExtendedState(Frame.MAXIMIZED_BOTH);
	}
	
	public static JFrame getFrame()
	{
		return frame;
	}
}
