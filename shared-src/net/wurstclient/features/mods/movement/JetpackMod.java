/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.movement;

import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.wurstclient.compatibility.WConnection;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Feature;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;
import net.wurstclient.settings.CheckboxSetting;

@SearchTags({"jet pack"})
@Mod.Bypasses(ghostMode = false,
	latestNCP = false,
	olderNCP = false,
	antiCheat = false,
	mineplex = false)
public final class JetpackMod extends Mod implements UpdateListener
{
	private final CheckboxSetting flightKickBypass = WMinecraft.COOLDOWN ? null
		: new CheckboxSetting("Flight-Kick-Bypass", false);
	private double flyHeight;
	
	public JetpackMod()
	{
		super("Jetpack", "Allows you to fly as if you had a jetpack.\n\n"
			+ "§c§lWARNING:§r You will take fall damage if you don't use NoFall.");
		setCategory(Category.MOVEMENT);
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.mods.noFallMod};
	}
	
	@Override
	public String getRenderName()
	{
		if(flightKickBypass == null || !flightKickBypass.isChecked())
			return getName();
		
		return getName() + "[Kick: " + (flyHeight <= 300 ? "Safe" : "Unsafe")
			+ "]";
	}
	
	@Override
	public void initSettings()
	{
		if(flightKickBypass != null)
			addSetting(flightKickBypass);
	}
	
	@Override
	public void onEnable()
	{
		wurst.mods.flightMod.setEnabled(false);
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
		if(mc.gameSettings.keyBindJump.pressed)
			WMinecraft.getPlayer().jump();
		
		if(flightKickBypass != null && flightKickBypass.isChecked())
		{
			updateMS();
			
			updateFlyHeight();
			WConnection.sendPacket(new CPacketPlayer(true));
			
			if(flyHeight <= 290 && hasTimePassedM(500)
				|| flyHeight > 290 && hasTimePassedM(100))
			{
				goToGround();
				updateLastMS();
			}
		}
	}
	
	private void updateFlyHeight()
	{
		double h = 1;
		AxisAlignedBB box = WMinecraft.getPlayer().getEntityBoundingBox()
			.expand(0.0625, 0.0625, 0.0625);
		for(flyHeight = 0; flyHeight < WMinecraft.getPlayer().posY; flyHeight +=
			h)
		{
			AxisAlignedBB nextBox = box.offset(0, -flyHeight, 0);
			
			if(WMinecraft.getWorld().checkBlockCollision(nextBox))
			{
				if(h < 0.0625)
					break;
				
				flyHeight -= h;
				h /= 2;
			}
		}
	}
	
	private void goToGround()
	{
		if(flyHeight > 300)
			return;
		
		double minY = WMinecraft.getPlayer().posY - flyHeight;
		
		if(minY <= 0)
			return;
		
		for(double y = WMinecraft.getPlayer().posY; y > minY;)
		{
			y -= 8;
			if(y < minY)
				y = minY;
			
			CPacketPlayer.Position packet =
				new CPacketPlayer.Position(WMinecraft.getPlayer().posX, y,
					WMinecraft.getPlayer().posZ, true);
			WConnection.sendPacket(packet);
		}
		
		for(double y = minY; y < WMinecraft.getPlayer().posY;)
		{
			y += 8;
			if(y > WMinecraft.getPlayer().posY)
				y = WMinecraft.getPlayer().posY;
			
			CPacketPlayer.Position packet =
				new CPacketPlayer.Position(WMinecraft.getPlayer().posX, y,
					WMinecraft.getPlayer().posZ, true);
			WConnection.sendPacket(packet);
		}
	}
}
