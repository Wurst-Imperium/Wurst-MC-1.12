/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.retro;

import net.minecraft.network.play.client.CPacketPlayer;
import net.wurstclient.compatibility.WConnection;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Mod;
import net.wurstclient.features.RetroMod;
import net.wurstclient.features.SearchTags;
import net.wurstclient.utils.EntityUtils;
import net.wurstclient.utils.EntityUtils.TargetSettings;

@SearchTags({"force push", "paralyze"})
@Mod.Bypasses
public final class ForcePushMod extends RetroMod implements UpdateListener
{
	private TargetSettings targetSettings = new TargetSettings()
	{
		@Override
		public boolean targetBehindWalls()
		{
			return true;
		}
		
		@Override
		public float getRange()
		{
			return 1F;
		}
	};
	
	public ForcePushMod()
	{
		super("ForcePush", "Pushes nearby mobs away from you.\n"
			+ "Can sometimes get you kicked for \"Flying is not enabled\".");
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
		if(WMinecraft.getPlayer().onGround
			&& EntityUtils.getClosestEntity(targetSettings) != null)
			for(int i = 0; i < 1000; i++)
				WConnection.sendPacket(new CPacketPlayer(true));
	}
}
