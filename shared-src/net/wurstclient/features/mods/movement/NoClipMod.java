/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.movement;

import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;

@SearchTags({"no clip"})
@Mod.Bypasses(ghostMode = false, latestNCP = false, olderNCP = false)
public final class NoClipMod extends Mod implements UpdateListener
{
	public NoClipMod()
	{
		super("NoClip", "Allows you to freely move through blocks.\n"
			+ "A block (e.g. sand) must fall on your head to activate it.\n\n"
			+ "§c§lWARNING:§r You will take damage while moving through blocks!");
		setCategory(Category.MOVEMENT);
	}
	
	@Override
	public void onEnable()
	{
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
		WMinecraft.getPlayer().noClip = false;
	}
	
	@Override
	public void onUpdate()
	{
		WMinecraft.getPlayer().noClip = true;
		WMinecraft.getPlayer().fallDistance = 0;
		WMinecraft.getPlayer().onGround = false;
		
		WMinecraft.getPlayer().capabilities.isFlying = false;
		WMinecraft.getPlayer().motionX = 0;
		WMinecraft.getPlayer().motionY = 0;
		WMinecraft.getPlayer().motionZ = 0;
		
		float speed = 0.2F;
		WMinecraft.getPlayer().jumpMovementFactor = speed;
		if(mc.gameSettings.keyBindJump.pressed)
			WMinecraft.getPlayer().motionY += speed;
		if(mc.gameSettings.keyBindSneak.pressed)
			WMinecraft.getPlayer().motionY -= speed;
	}
}
