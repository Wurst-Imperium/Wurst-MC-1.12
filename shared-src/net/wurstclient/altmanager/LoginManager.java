/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.altmanager;

import java.net.Proxy;

import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

public final class LoginManager
{
	public static String login(String email, String password)
	{
		YggdrasilUserAuthentication auth =
			(YggdrasilUserAuthentication)new YggdrasilAuthenticationService(
				Proxy.NO_PROXY, "").createUserAuthentication(Agent.MINECRAFT);
		
		auth.setUsername(email);
		auth.setPassword(password);
		
		try
		{
			auth.logIn();
			Minecraft.getMinecraft().session =
				new Session(auth.getSelectedProfile().getName(),
					auth.getSelectedProfile().getId().toString(),
					auth.getAuthenticatedToken(), "mojang");
			return "";
			
		}catch(AuthenticationUnavailableException e)
		{
			return "§4§lCannot contact authentication server!";
			
		}catch(AuthenticationException e)
		{
			e.printStackTrace();
			if(e.getMessage().contains("Invalid username or password.")
				|| e.getMessage().toLowerCase().contains("account migrated"))
				return "§4§lWrong password!";
			else
				return "§4§lCannot contact authentication server!";
			
		}catch(NullPointerException e)
		{
			return "§4§lWrong password!";
		}
	}
	
	public static void changeCrackedName(String newName)
	{
		Minecraft.getMinecraft().session =
			new Session(newName, "", "", "mojang");
	}
}
