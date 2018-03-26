/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.wurstclient.WurstClient;
import net.wurstclient.keybinds.PossibleKeybind;
import net.wurstclient.settings.Setting;

public abstract class Feature
{
	protected static final WurstClient wurst = WurstClient.INSTANCE;
	protected static final Minecraft mc = Minecraft.getMinecraft();
	
	private Category category;
	
	private final ArrayList<Setting> settings = new ArrayList<>();
	
	private final String helpPage =
		getClass().isAnnotationPresent(HelpPage.class)
			? getClass().getAnnotation(HelpPage.class).value() : "";
	private final String searchTags =
		getClass().isAnnotationPresent(SearchTags.class) ? String.join("§",
			getClass().getAnnotation(SearchTags.class).value()) : "";
	
	public abstract String getName();
	
	public abstract String getType();
	
	public final Category getCategory()
	{
		return category;
	}
	
	protected final void setCategory(Category category)
	{
		this.category = category;
	}
	
	public abstract String getDescription();
	
	public abstract boolean isEnabled();
	
	public abstract boolean isBlocked();
	
	public final ArrayList<Setting> getSettings()
	{
		return settings;
	}
	
	protected final void addSetting(Setting setting)
	{
		settings.add(setting);
	}
	
	public abstract ArrayList<PossibleKeybind> getPossibleKeybinds();
	
	public abstract String getPrimaryAction();
	
	public abstract void doPrimaryAction();
	
	public final String getHelpPage()
	{
		return helpPage;
	}
	
	public final String getSearchTags()
	{
		return searchTags;
	}
	
	public abstract Feature[] getSeeAlso();
}
