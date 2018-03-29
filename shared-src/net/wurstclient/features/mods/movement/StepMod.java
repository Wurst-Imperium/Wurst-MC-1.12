/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.movement;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.wurstclient.compatibility.WConnection;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Mod;
import net.wurstclient.features.special_features.YesCheatSpf.Profile;
import net.wurstclient.settings.ModeSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;

@Mod.Bypasses(ghostMode = false)
public final class StepMod extends Mod implements UpdateListener
{
	private final ModeSetting mode = new ModeSetting("Mode",
		"§lSimple§r mode can step up multiple blocks (enables Height slider).\n"
			+ "§lLegit§r mode can bypass NoCheat+.",
		new String[]{"Simple", "Legit"}, 1)
	{
		@Override
		public void update()
		{
			height.setDisabled(getSelected() == 1);
		}
	};
	private final SliderSetting height =
		new SliderSetting("Height", 1, 1, 100, 1, ValueDisplay.INTEGER);
	
	public StepMod()
	{
		super("Step", "Allows you to step up full blocks.");
		setCategory(Category.MOVEMENT);
	}
	
	@Override
	public void initSettings()
	{
		addSetting(mode);
		addSetting(height);
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
		WMinecraft.getPlayer().stepHeight = 0.5F;
	}
	
	@Override
	public void onUpdate()
	{
		if(mode.getSelected() == 0)
		{
			// simple mode
			WMinecraft.getPlayer().stepHeight = height.getValueF();
			return;
		}
		
		// legit mode
		EntityPlayerSP player = WMinecraft.getPlayer();
		player.stepHeight = 0.5F;
		
		if(!player.isCollidedHorizontally)
			return;
		
		if(!player.onGround || player.isOnLadder() || player.isInWater()
			|| player.isInLava())
			return;
		
		if(player.movementInput.moveForward == 0
			&& player.movementInput.moveStrafe == 0)
			return;
		
		if(player.movementInput.jump)
			return;
		
		AxisAlignedBB box =
			player.getEntityBoundingBox().offset(0, 0.05, 0).expandXyz(0.05);
		
		if(!WMinecraft.getWorld().getCollisionBoxes(player, box.offset(0, 1, 0))
			.isEmpty())
			return;
		
		double stepHeight = -1;
		for(AxisAlignedBB bb : WMinecraft.getWorld().getCollisionBoxes(player,
			box))
			if(bb.maxY > stepHeight)
				stepHeight = bb.maxY;
		stepHeight = stepHeight - player.posY;
		
		if(stepHeight < 0 || stepHeight > 1)
			return;
		
		WConnection.sendPacket(new CPacketPlayer.Position(player.posX,
			player.posY + 0.42 * stepHeight, player.posZ, player.onGround));
		WConnection.sendPacket(new CPacketPlayer.Position(player.posX,
			player.posY + 0.753 * stepHeight, player.posZ, player.onGround));
		player.setPosition(player.posX, player.posY + 1 * stepHeight,
			player.posZ);
	}
	
	@Override
	public void onYesCheatUpdate(Profile profile)
	{
		switch(profile)
		{
			default:
			case OFF:
			case MINEPLEX:
			mode.unlock();
			break;
			
			case ANTICHEAT:
			case OLDER_NCP:
			case LATEST_NCP:
			mode.lock(1);
			break;
		}
	}
	
	public boolean isAutoJumpAllowed()
	{
		return !isActive() && !wurst.commands.goToCmd.isActive();
	}
}
