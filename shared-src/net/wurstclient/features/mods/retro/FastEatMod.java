/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.retro;

import net.minecraft.item.ItemFood;
import net.minecraft.network.play.client.CPacketPlayer;
import net.wurstclient.compatibility.WConnection;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Mod;
import net.wurstclient.features.RetroMod;
import net.wurstclient.features.SearchTags;
import net.wurstclient.utils.InventoryUtils;

@SearchTags({"FastNom", "fast eat", "fast nom"})
@Mod.Bypasses(ghostMode = false, latestNCP = false, olderNCP = false)
public final class FastEatMod extends RetroMod implements UpdateListener
{
	public FastEatMod()
	{
		super("FastEat",
			"Allows you to eat food much faster.\n" + "OM! NOM! NOM!");
		setCategory(Category.RETRO);
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
	}
	
	@Override
	public void onUpdate()
	{
		// check if alive
		if(WMinecraft.getPlayer().getHealth() <= 0)
			return;
		
		// check onGround
		if(!WMinecraft.getPlayer().onGround)
			return;
		
		// check if eating
		if(!mc.gameSettings.keyBindUseItem.pressed)
			return;
		
		// check hunger level
		if(!WMinecraft.getPlayer().getFoodStats().needFood())
			return;
		
		// check held item
		if(!InventoryUtils.checkHeldItem((item) -> item instanceof ItemFood))
			return;
		
		// send packets
		for(int i = 0; i < 100; i++)
			WConnection.sendPacket(new CPacketPlayer(false));
	}
}
