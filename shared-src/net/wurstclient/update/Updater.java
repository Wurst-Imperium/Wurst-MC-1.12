/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.update;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.wurstclient.WurstClient;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.utils.ChatUtils;

public final class Updater implements UpdateListener
{
	private boolean outdated;
	private String latestVersionString;
	private ITextComponent component;
	
	@Override
	public void onUpdate()
	{
		if(component != null)
			ChatUtils.component(component);
		
		WurstClient.INSTANCE.events.remove(UpdateListener.class, this);
	}
	
	public void checkForUpdate()
	{
		Version currentVersion = new Version(WurstClient.VERSION);
		Version latestVersion = null;
		
		try
		{
			JsonArray json = fetchJson(
				"https://api.github.com/repos/Wurst-Imperium/Wurst-MCX2/releases")
					.getAsJsonArray();
			
			for(JsonElement element : json)
			{
				JsonObject release = element.getAsJsonObject();
				
				if(!currentVersion.isPreRelease()
					&& release.get("prerelease").getAsBoolean())
					continue;
				
				if(!containsCompatibleAsset(
					release.get("assets").getAsJsonArray()))
					continue;
				
				latestVersionString =
					release.get("tag_name").getAsString().substring(1);
				latestVersion = new Version(latestVersionString);
				break;
			}
			
			if(latestVersion == null)
				throw new NullPointerException("Latest version is missing!");
			
			System.out.println("[Updater] Current version: " + currentVersion);
			System.out.println("[Updater] Latest version: " + latestVersion);
			outdated = currentVersion.shouldUpdateTo(latestVersion);
			
		}catch(Exception e)
		{
			System.err.println("[Updater] An error occurred!");
			e.printStackTrace();
		}
		
		if(latestVersion == null || latestVersion.isInvalid())
		{
			component = new TextComponentString(
				"An error occurred while checking for updates."
					+ " Click \u00a7nhere\u00a7r to check manually.");
			ClickEvent event = new ClickEvent(ClickEvent.Action.OPEN_URL,
				"https://www.wurstclient.net/download/");
			component.getStyle().setClickEvent(event);
			WurstClient.INSTANCE.events.add(UpdateListener.class, this);
			return;
		}
		
		if(!latestVersion.isHigherThan(WurstClient.VERSION))
			return;
		
		component = new TextComponentString(
			"Wurst " + latestVersion + " is now available."
				+ " Click \u00a7nhere\u00a7r to download the update.");
		ClickEvent event = new ClickEvent(ClickEvent.Action.OPEN_URL,
			"https://www.wurstclient.net/download/");
		component.getStyle().setClickEvent(event);
		WurstClient.INSTANCE.events.add(UpdateListener.class, this);
	}
	
	private boolean containsCompatibleAsset(JsonArray assets)
	{
		for(JsonElement asset : assets)
		{
			if(!asset.getAsJsonObject().get("name").getAsString()
				.endsWith("MC" + WMinecraft.VERSION
					+ (WMinecraft.OPTIFINE ? "-OF" : "") + ".jar"))
				continue;
			
			return true;
		}
		
		return false;
	}
	
	private JsonElement fetchJson(String url) throws IOException
	{
		URI u = URI.create(url);
		try(InputStream in = u.toURL().openStream())
		{
			return new JsonParser()
				.parse(new BufferedReader(new InputStreamReader(in)));
		}
	}
	
	public void update()
	{
		new Thread(() -> {
			try
			{
				Path path = Paths
					.get(Updater.class.getProtectionDomain().getCodeSource()
						.getLocation().toURI())
					.getParent().resolve("Wurst-updater.jar");
				
				try(InputStream in =
					getClass().getClassLoader().getResourceAsStream(
						"assets/minecraft/wurst/Wurst-updater.jar"))
				{
					Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
				}
				
				ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "java",
					"-jar", path.toString(), "update",
					path.getParent().toString(), latestVersionString,
					WMinecraft.VERSION + (WMinecraft.OPTIFINE ? "-OF" : ""));
				pb.redirectErrorStream(true);
				Process p = pb.start();
				
				try(BufferedReader reader = new BufferedReader(
					new InputStreamReader(p.getInputStream())))
				{
					for(String line; (line = reader.readLine()) != null;)
						System.out.println(line);
				}
				
			}catch(Exception e)
			{
				System.err.println("Could not update!");
				e.printStackTrace();
			}
		}).start();
	}
	
	public boolean isOutdated()
	{
		return outdated;
	}
	
	public String getLatestVersion()
	{
		return latestVersionString;
	}
}
