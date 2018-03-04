/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.render;

import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;
import net.wurstclient.settings.CheckboxSetting;

@SearchTags({"NightVision", "full bright", "brightness", "night vision"})
@Mod.Bypasses
public final class FullbrightMod extends Mod implements UpdateListener
{
	private final CheckboxSetting shaderMode = WMinecraft.OPTIFINE
		? new CheckboxSetting("Shader compatibility mode", false)
		{
			@Override
			public void update()
			{
				if(isActive())
					mc.renderGlobal.loadRenderers();
			}
		} : null;
	
	public FullbrightMod()
	{
		super("Fullbright", "Allows you to see in the dark.");
		setCategory(Category.RENDER);
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void initSettings()
	{
		if(shaderMode != null)
			addSetting(shaderMode);
	}
	
	@Override
	public void onEnable()
	{
		if(shaderMode != null && shaderMode.isChecked())
			mc.renderGlobal.loadRenderers();
	}
	
	@Override
	public void onDisable()
	{
		if(shaderMode != null && shaderMode.isChecked())
			mc.renderGlobal.loadRenderers();
		
		if(wurst.mods.panicMod.isActive())
			mc.gameSettings.gammaSetting = 0.5F;
	}
	
	@Override
	public void onUpdate()
	{
		if(isActive() || wurst.mods.xRayMod.isActive())
		{
			if(mc.gameSettings.gammaSetting < 16F)
				mc.gameSettings.gammaSetting += 0.5F;
		}else if(mc.gameSettings.gammaSetting > 0.5F)
			if(mc.gameSettings.gammaSetting < 1F)
				mc.gameSettings.gammaSetting = 0.5F;
			else
				mc.gameSettings.gammaSetting -= 0.5F;
	}
	
	public boolean useShaderMode()
	{
		return isActive() && shaderMode != null && shaderMode.isChecked();
	}
}
