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
import net.wurstclient.features.special_features.YesCheatSpf.Profile;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.ModeSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;

@SearchTags({"FlyHack", "fly hack", "flying"})
@Mod.Bypasses(ghostMode = false, latestNCP = false)
public final class FlightMod extends Mod implements UpdateListener
{
	private final ModeSetting mode = new ModeSetting("Mode",
		new String[]{"Normal", "Mineplex", "Old NCP"}, 0)
	{
		@Override
		public void update()
		{
			if(getSelected() > 0)
			{
				speed.setDisabled(true);
				if(flightKickBypass != null)
					flightKickBypass.lock(() -> false);
			}else
			{
				speed.setDisabled(false);
				if(flightKickBypass != null)
					flightKickBypass.unlock();
			}
		}
	};
	public final SliderSetting speed =
		new SliderSetting("Speed", 1, 0.05, 5, 0.05, ValueDisplay.DECIMAL);
	private final CheckboxSetting flightKickBypass = WMinecraft.COOLDOWN ? null
		: new CheckboxSetting("Flight-Kick-Bypass", false);
	
	private double flyHeight;
	private double startY;
	
	public FlightMod()
	{
		super("Flight", "Allows you to you fly.\n\n"
			+ "§c§lWARNING:§r You will take fall damage if you don't use NoFall.");
		setCategory(Category.MOVEMENT);
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
		addSetting(mode);
		addSetting(speed);
		
		if(flightKickBypass != null)
			addSetting(flightKickBypass);
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.mods.noFallMod, wurst.mods.jetpackMod,
			wurst.mods.glideMod, wurst.special.yesCheatSpf};
	}
	
	@Override
	public void onEnable()
	{
		wurst.mods.jetpackMod.setEnabled(false);
		
		if(mode.getSelected() > 0)
		{
			double startX = WMinecraft.getPlayer().posX;
			startY = WMinecraft.getPlayer().posY;
			double startZ = WMinecraft.getPlayer().posZ;
			
			for(int i = 0; i < 4; i++)
			{
				WConnection.sendPacket(new CPacketPlayer.Position(startX,
					startY + 1.01, startZ, false));
				WConnection.sendPacket(
					new CPacketPlayer.Position(startX, startY, startZ, false));
			}
			
			WMinecraft.getPlayer().jump();
		}
		
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
		switch(mode.getSelected())
		{
			case 0:
			// Normal
			WMinecraft.getPlayer().capabilities.isFlying = false;
			WMinecraft.getPlayer().motionX = 0;
			WMinecraft.getPlayer().motionY = 0;
			WMinecraft.getPlayer().motionZ = 0;
			WMinecraft.getPlayer().jumpMovementFactor = speed.getValueF();
			
			if(mc.gameSettings.keyBindJump.pressed)
				WMinecraft.getPlayer().motionY += speed.getValue();
			if(mc.gameSettings.keyBindSneak.pressed)
				WMinecraft.getPlayer().motionY -= speed.getValue();
			
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
			break;
			
			case 1:
			// Mineplex
			updateMS();
			if(!WMinecraft.getPlayer().onGround)
				if(mc.gameSettings.keyBindJump.pressed && hasTimePassedS(2))
				{
					WMinecraft.getPlayer().setPosition(
						WMinecraft.getPlayer().posX,
						WMinecraft.getPlayer().posY + 8,
						WMinecraft.getPlayer().posZ);
					updateLastMS();
				}else if(mc.gameSettings.keyBindSneak.pressed)
					WMinecraft.getPlayer().motionY = -0.4;
				else
					WMinecraft.getPlayer().motionY = -0.02;
			WMinecraft.getPlayer().jumpMovementFactor = 0.04F;
			break;
			
			case 2:
			// Old NCP
			if(!WMinecraft.getPlayer().onGround)
				if(mc.gameSettings.keyBindJump.pressed
					&& WMinecraft.getPlayer().posY < startY - 1)
					WMinecraft.getPlayer().motionY = 0.2;
				else
					WMinecraft.getPlayer().motionY = -0.02;
			break;
		}
	}
	
	@Override
	public void onYesCheatUpdate(Profile profile)
	{
		switch(profile)
		{
			case OFF:
			default:
			mode.unlock();
			break;
			
			case MINEPLEX:
			mode.lock(1);
			break;
			
			case ANTICHEAT:
			case OLDER_NCP:
			mode.lock(2);
			break;
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
