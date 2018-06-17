/*
 * Copyright © 2017 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import net.minecraft.block.Block;
import net.wurstclient.clickgui.BlockListEditButton;
import net.wurstclient.clickgui.Component;
import net.wurstclient.compatibility.WBlock;
import net.wurstclient.files.ConfigFiles;
import net.wurstclient.keybinds.PossibleKeybind;

public final class BlockListSetting extends Setting
{
	private final ArrayList<String> blockNames = new ArrayList<>();
	private final String[] defaultNames;
	
	public BlockListSetting(String name, String description, Block... blocks)
	{
		super(name, description);
		
		Arrays.stream(blocks).parallel().filter(Objects::nonNull)
			.map(b -> WBlock.getName(b)).distinct().sorted()
			.forEachOrdered(s -> blockNames.add(s));
		defaultNames = blockNames.toArray(new String[0]);
	}
	
	public BlockListSetting(String name, Block... blocks)
	{
		this(name, null, blocks);
	}
	
	public List<String> getBlockNames()
	{
		return Collections.unmodifiableList(blockNames);
	}
	
	public void add(Block block)
	{
		String name = WBlock.getName(block);
		if(Collections.binarySearch(blockNames, name) >= 0)
			return;
		
		blockNames.add(name);
		Collections.sort(blockNames);
		ConfigFiles.SETTINGS.save();
	}
	
	public void remove(int index)
	{
		if(index < 0 || index >= blockNames.size())
			return;
		
		blockNames.remove(index);
		ConfigFiles.SETTINGS.save();
	}
	
	public void resetToDefaults()
	{
		blockNames.clear();
		blockNames.addAll(Arrays.asList(defaultNames));
		ConfigFiles.SETTINGS.save();
	}
	
	@Override
	public Component getComponent()
	{
		return new BlockListEditButton(this);
	}
	
	@Override
	public void fromJson(JsonElement json)
	{
		if(!json.isJsonArray())
			return;
		
		blockNames.clear();
		StreamSupport.stream(json.getAsJsonArray().spliterator(), true)
			.filter(e -> e.isJsonPrimitive())
			.filter(e -> e.getAsJsonPrimitive().isString())
			.map(e -> Block.getBlockFromName(e.getAsString()))
			.filter(Objects::nonNull).map(b -> WBlock.getName(b)).distinct()
			.sorted().forEachOrdered(s -> blockNames.add(s));
	}
	
	@Override
	public JsonElement toJson()
	{
		JsonArray json = new JsonArray();
		blockNames.forEach(s -> json.add(new JsonPrimitive(s)));
		return json;
	}
	
	@Override
	public ArrayList<PossibleKeybind> getPossibleKeybinds(String featureName)
	{
		return new ArrayList<>();
	}
}
