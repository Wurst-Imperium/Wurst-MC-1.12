/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.fun;

import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.wurstclient.compatibility.WPlayer;
import net.wurstclient.compatibility.WPotionEffects;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Mod;

@Mod.Bypasses
@Mod.DontSaveState
public final class LsdMod extends Mod implements UpdateListener
{
	public LsdMod()
	{
		super("LSD", "Causes hallucinations.");
		setCategory(Category.FUN);
	}
	
	@Override
	public void onEnable()
	{
		if(!OpenGlHelper.shadersSupported)
		{
			wurst.events.add(UpdateListener.class, this);
			return;
		}
		
		if(!(mc.getRenderViewEntity() instanceof EntityPlayer))
		{
			setEnabled(false);
			return;
		}
		
		if(mc.entityRenderer.theShaderGroup != null)
			mc.entityRenderer.theShaderGroup.deleteShaderGroup();
		mc.entityRenderer.shaderIndex = 19;
		mc.entityRenderer.loadShader(EntityRenderer.SHADERS_TEXTURES[19]);
	}
	
	@Override
	public void onDisable()
	{
		if(!OpenGlHelper.shadersSupported)
		{
			wurst.events.remove(UpdateListener.class, this);
			WPlayer.removePotionEffect(WPotionEffects.NAUSEA);
			return;
		}
		
		if(mc.entityRenderer.theShaderGroup != null)
		{
			mc.entityRenderer.theShaderGroup.deleteShaderGroup();
			mc.entityRenderer.theShaderGroup = null;
		}
	}
	
	@Override
	public void onUpdate()
	{
		if(!OpenGlHelper.shadersSupported)
			WPlayer.addPotionEffect(WPotionEffects.NAUSEA);
	}
}
