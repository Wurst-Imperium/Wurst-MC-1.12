/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.spam;

import java.awt.HeadlessException;
import java.io.*;

import javax.swing.JOptionPane;

import net.wurstclient.WurstClient;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.features.mods.chat.SpammerMod;
import net.wurstclient.files.WurstFolders;
import net.wurstclient.hooks.FrameHook;
import net.wurstclient.spam.exceptions.InvalidVariableException;
import net.wurstclient.spam.exceptions.SpamException;
import net.wurstclient.spam.exceptions.UnreadableTagException;
import net.wurstclient.spam.exceptions.UnreadableVariableException;
import net.wurstclient.spam.tag.TagData;
import net.wurstclient.spam.tag.TagManager;
import net.wurstclient.utils.MiscUtils;

public class SpamProcessor
{
	public static TagManager tagManager = new TagManager();
	public static VarManager varManager = new VarManager();
	
	public static void runScript(final String filename,
		final String description)
	{
		if(!WurstClient.INSTANCE.isEnabled())
			return;
		
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				File file = new File(WurstFolders.SCRIPTS.toFile(),
					filename + ".wspam");
				try
				{
					long startTime = System.currentTimeMillis();
					while(!canSpam())
					{
						Thread.sleep(50);
						if(System.currentTimeMillis() > startTime + 10000)
							return;
					}
					if(!file.getParentFile().exists())
						file.getParentFile().mkdirs();
					if(!file.exists())
					{
						PrintWriter save =
							new PrintWriter(new OutputStreamWriter(
								new FileOutputStream(file), "UTF-8"));
						save.println("<!--");
						for(String line : description.split("\n"))
							save.println("  " + line);
						save.println("-->");
						save.close();
					}
					runFile(file);
				}catch(Exception e)
				{
					e.printStackTrace();
					StringWriter tracewriter = new StringWriter();
					e.printStackTrace(new PrintWriter(tracewriter));
					String message = "An error occurred while running "
						+ file.getName() + ":\n" + e.getLocalizedMessage()
						+ "\n" + tracewriter.toString();
					JOptionPane.showMessageDialog(FrameHook.getFrame(), message,
						"Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}).start();
	}
	
	public static boolean runSpam(final String filename)
	{
		final File file =
			new File(WurstFolders.SPAM.toFile(), filename + ".wspam");
		if(!file.exists())
			return false;
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					long startTime = System.currentTimeMillis();
					while(!canSpam())
					{
						Thread.sleep(50);
						if(System.currentTimeMillis() > startTime + 10000)
							return;
					}
					runFile(file);
				}catch(Exception e)
				{
					if(e instanceof NullPointerException
						&& WMinecraft.getPlayer() == null)
						return;
					e.printStackTrace();
					StringWriter tracewriter = new StringWriter();
					e.printStackTrace(new PrintWriter(tracewriter));
					String message = "An error occurred while running "
						+ file.getName() + ":\n" + e.getLocalizedMessage()
						+ "\n" + tracewriter.toString();
					JOptionPane.showMessageDialog(FrameHook.getFrame(), message,
						"Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}).start();
		return true;
	}
	
	private static void runFile(File file) throws Exception
	{
		try
		{
			BufferedReader load = new BufferedReader(
				new InputStreamReader(new FileInputStream(file), "UTF-8"));
			String content = load.readLine();
			for(String line = ""; (line = load.readLine()) != null;)
				content += "\n" + line;
			load.close();
			String spam = SpamProcessor.process(content, null, false);
			if(spam == null || spam.isEmpty())
				return;
			for(int i = 0; i < spam.split("\n").length; i++)
			{
				WMinecraft.getPlayer()
					.sendAutomaticChatMessage(spam.split("\n")[i]);
				Thread.sleep(WurstClient.INSTANCE.options.spamDelay);
			}
		}catch(NullPointerException e)
		{
			if(WMinecraft.getPlayer() != null)
				throw e;
		}
	}
	
	private static boolean canSpam()
	{
		return WMinecraft.getPlayer() != null && WMinecraft.getWorld() != null;
	}
	
	public static String process(String spam, SpammerMod spammerMod,
		boolean test)
	{
		try
		{
			log("### Cleaning up variables...");
			varManager.clearUserVars();
			log("### Processing spam...");
			final String source = new String(spam);
			log("### Processing comments...");
			if(test)
				spam = spam.replace("<!--", "#!--");
			else
				spam = spam.replaceAll("(?s)<!--.*?-->", "");
			log("** Processed comments:\n" + spam);
			log("### Processing tags...");
			while(spam.contains("<"))
			{
				log("** Processing tag...");
				int tagStart = spam.indexOf("<");
				log("TagStart: " + tagStart);
				int tagLine =
					MiscUtils.countMatches(spam.substring(0, tagStart), "\n");
				log("TagLine: " + tagLine);
				String tag;
				String tagName = null;
				tag = spam.substring(tagStart);
				try
				{
					tagName = tag.substring(1, tag.indexOf(">")).split(" ")[0];
				}catch(StringIndexOutOfBoundsException e1)
				{
					throw new UnreadableTagException(source.substring(tagStart),
						tagLine);
				}
				log("TagName: " + tagName);
				String[] tagArgs;
				try
				{
					tagArgs =
						tag.substring(tagName.length() + 2, tag.indexOf(">"))
							.split(" ");
				}catch(StringIndexOutOfBoundsException e)
				{
					tagArgs = new String[0];
				}
				log("TagArgs:");
				for(int i = 0; i < tagArgs.length; i++)
					log("No. " + i + ": " + tagArgs[i]);
				String tmpTag = new String(tag);
				int tmpSubTags = 0;
				int tagLength = tag.length();
				boolean tagClosed = false;
				int tagContentLength = tag.length();
				log("+ Calculating TagLength...");
				while(tmpTag.contains("<"))
				{
					log("Found subtag: "
						+ tmpTag.substring(tmpTag.indexOf("<"),
							tmpTag.indexOf("<") + 2)
						+ " at " + tmpTag.indexOf("<"));
					if(tmpTag.substring(tmpTag.indexOf("<") + 1,
						tmpTag.indexOf("<") + 2).equals("/"))
						tmpSubTags--;
					else
						tmpSubTags++;
					log("Subtags left: " + tmpSubTags);
					if(tmpSubTags == 0)
					{
						tagLength = tmpTag.indexOf("<") + tagName.length() + 3;
						tagContentLength = tmpTag.indexOf("<");
						log("TagContentLength: " + tagContentLength);
						tmpTag = tmpTag.replaceFirst("<", "#");
						tagClosed = true;
						break;
					}
					tmpTag = tmpTag.replaceFirst("<", "#");
				}
				log("TagLength: " + tagLength);
				tag = tag.substring(0, tagLength);
				log("Raw Tag:\n" + tag);
				String tagContent =
					tag.substring(tag.indexOf(">") + 1, tagContentLength);
				log("TagContent: " + tagContent);
				TagData tagData =
					new TagData(tagStart, tagLength, tagLine, tagName, tagArgs,
						tagClosed, tag, tagContent, tagContentLength, spam);
				String tagReplacement = tagManager.process(tagData);
				if(test)
					spam = spam.substring(0, tagStart)
						+ (tagClosed
							? tag.replaceFirst("<", "#")
								.replaceFirst("(?s)(.*)<", "$1#")
							: tag.replaceFirst("<", "#"))
						+ spam.substring(tagStart + tagLength, spam.length());
				else
					spam = spam.substring(0, tagStart) + tagReplacement
						+ spam.substring(tagStart + tagLength, spam.length());
				log("** Processed tag:\n" + spam);
			}
			log("### Processing variables...");
			while(spam.contains("§"))
			{
				log("** Processing variable...");
				int varStart = spam.indexOf("§");
				log("VarStart: " + varStart);
				int varLine =
					MiscUtils.countMatches(spam.substring(0, varStart), "\n");
				log("VarLine: " + varLine);
				int varEnd = spam.indexOf(";", varStart) + 1;
				log("VarEnd: " + varEnd);
				String var = spam.substring(varStart);
				try
				{
					if(varEnd <= 0)
						throw new Exception();
					var = spam.substring(varStart, varEnd);
				}catch(Exception e)
				{
					throw new UnreadableVariableException(
						source.substring(varStart), varLine);
				}
				log("Var: " + var);
				String varName = spam.substring(varStart + 1, varEnd - 1);
				log("VarName: " + varName);
				log("** Processed variable:\n" + spam);
				String varReplacement = varManager.getValueOfVar(varName);
				if(varReplacement == null)
					throw new InvalidVariableException(varName, varLine);
				if(test)
					spam = spam.substring(0, varStart) + var.replace("§", "*")
						+ spam.substring(varEnd, spam.length());
				else
					spam = spam.substring(0, varStart) + varReplacement
						+ spam.substring(varEnd, spam.length());
			}
			log("### Final Spam:\n" + spam);
		}catch(SpamException e)
		{
			if(!test)
				return null;
			if(spammerMod == null)
			{
				e.printStackTrace();
				return null;
			}
			String message = e.getClass().getSimpleName() + " at line "
				+ (e.line + 1) + ":\n" + e.getMessage();
			switch(JOptionPane.showOptionDialog(spammerMod.getDialog(), message,
				"Error", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
				null, new String[]{"Go to line", "Show help"}, 0))
			{
				case 0:
				spammerMod.goToLine(e.line);
				break;
				case 1:
				try
				{
					JOptionPane.showOptionDialog(spammerMod.getDialog(),
						e.getHelp(), "Help", JOptionPane.DEFAULT_OPTION,
						JOptionPane.INFORMATION_MESSAGE, null,
						new String[]{"OK"}, 0);
				}catch(HeadlessException e1)
				{
					e1.printStackTrace();
				}
				break;
				
				default:
				break;
			}
			return null;
		}catch(Exception e)
		{
			System.err.println("Unknown exception in SpamProcessor:");
			e.printStackTrace();
			return null;
		}
		return spam;
	}
	
	private static void log(String log)
	{
		if(!"".isEmpty())// Manual switch for debugging
			System.out.println(log);
	}
}
