/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.files;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.JsonElement;

import net.wurstclient.utils.JsonUtils;

public abstract class Config
{
	private final Path path;
	
	public Config(String name)
	{
		path = WurstFolders.MAIN.resolve(name);
	}
	
	public final void initialize()
	{
		if(Files.exists(path))
			load();
		else
			save();
	}
	
	public final void load()
	{
		try
		{
			loadFromJson(readFile(path));
		}catch(Exception e)
		{
			System.out.println("Failed to load " + path.getFileName());
			e.printStackTrace();
		}
	}
	
	public final void save()
	{
		try
		{
			writeFile(path, saveToJson());
		}catch(Exception e)
		{
			System.out.println("Failed to save " + path.getFileName());
			e.printStackTrace();
		}
	}
	
	protected JsonElement readFile(Path path) throws IOException
	{
		try(BufferedReader reader = Files.newBufferedReader(path))
		{
			return JsonUtils.jsonParser.parse(reader);
		}
	}
	
	protected void writeFile(Path path, JsonElement json) throws IOException
	{
		try(BufferedWriter writer = Files.newBufferedWriter(path))
		{
			JsonUtils.prettyGson.toJson(json, writer);
		}
	}
	
	protected abstract void loadFromJson(JsonElement json);
	
	protected abstract JsonElement saveToJson();
}
