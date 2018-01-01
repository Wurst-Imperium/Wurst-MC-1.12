/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class ChatUtils
{
	private static boolean enabled = true;
	
	public static void setEnabled(boolean enabled)
	{
		ChatUtils.enabled = enabled;
	}
	
	public static void component(ITextComponent component)
	{
		if(enabled)
			Minecraft.getMinecraft().ingameGUI.getChatGUI()
				.printChatMessage(new TextComponentString("§c[§6Wurst§c]§f ")
					.appendSibling(component));
	}
	
	public static void message(String message)
	{
		component(new TextComponentString(message));
	}
	
	public static void warning(String message)
	{
		message("§c[§6§lWARNING§c]§f " + message);
	}
	
	public static void error(String message)
	{
		message("§c[§4§lERROR§c]§f " + message);
	}
	
	public static void success(String message)
	{
		message("§a[§2§lSUCCESS§a]§f " + message);
	}
	
	public static void failure(String message)
	{
		message("§c[§4§lFAILURE§c]§f " + message);
	}
	
	public static void cmd(String message)
	{
		Minecraft.getMinecraft().ingameGUI.getChatGUI()
			.printChatMessage(new TextComponentString(
				"§c[§6Wurst§c]§f §0§l<§aCMD§0§l>§f " + message));
	}
}
