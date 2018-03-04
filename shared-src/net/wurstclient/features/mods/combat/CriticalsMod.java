/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.combat;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.CPacketPlayer;
import net.wurstclient.compatibility.WConnection;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.LeftClickListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Feature;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;
import net.wurstclient.settings.ModeSetting;

@SearchTags({"Crits"})
@Mod.Bypasses(ghostMode = false)
public final class CriticalsMod extends Mod implements LeftClickListener
{
	private final ModeSetting mode =
		new ModeSetting("Mode", new String[]{"Jump", "Packet"}, 1);
	
	public CriticalsMod()
	{
		super("Criticals", "Changes all your hits to critical hits.");
		setCategory(Category.COMBAT);
	}
	
	@Override
	public void initSettings()
	{
		addSetting(mode);
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.mods.killauraMod, wurst.mods.triggerBotMod};
	}
	
	@Override
	public void onEnable()
	{
		wurst.events.add(LeftClickListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(LeftClickListener.class, this);
	}
	
	@Override
	public void onLeftClick(LeftClickEvent event)
	{
		if(mc.objectMouseOver == null
			|| !(mc.objectMouseOver.entityHit instanceof EntityLivingBase))
			return;
		
		doCritical();
	}
	
	public void doCritical()
	{
		if(!isActive())
			return;
		
		if(!WMinecraft.getPlayer().onGround)
			return;
		
		if(WMinecraft.getPlayer().isInWater()
			|| WMinecraft.getPlayer().isInLava())
			return;
		
		switch(mode.getSelected())
		{
			case 0:
			WMinecraft.getPlayer().motionY = 0.1F;
			WMinecraft.getPlayer().fallDistance = 0.1F;
			WMinecraft.getPlayer().onGround = false;
			break;
			
			case 1:
			double posX = WMinecraft.getPlayer().posX;
			double posY = WMinecraft.getPlayer().posY;
			double posZ = WMinecraft.getPlayer().posZ;
			WConnection.sendPacket(
				new CPacketPlayer.Position(posX, posY + 0.0625D, posZ, true));
			WConnection.sendPacket(
				new CPacketPlayer.Position(posX, posY, posZ, false));
			WConnection.sendPacket(
				new CPacketPlayer.Position(posX, posY + 1.1E-5D, posZ, false));
			WConnection.sendPacket(
				new CPacketPlayer.Position(posX, posY, posZ, false));
			break;
		}
	}
}
