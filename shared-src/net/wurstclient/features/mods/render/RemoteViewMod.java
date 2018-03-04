/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.render;

import net.minecraft.entity.Entity;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Feature;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;
import net.wurstclient.utils.ChatUtils;
import net.wurstclient.utils.EntityFakePlayer;
import net.wurstclient.utils.EntityUtils;
import net.wurstclient.utils.EntityUtils.TargetSettings;

@SearchTags({"remote view"})
@Mod.Bypasses
@Mod.DontSaveState
public final class RemoteViewMod extends Mod implements UpdateListener
{
	private Entity entity = null;
	private boolean wasInvisible;
	
	private EntityFakePlayer fakePlayer;
	
	private final TargetSettings targetSettingsFind = new TargetSettings()
	{
		@Override
		public boolean targetFriends()
		{
			return true;
		}
		
		@Override
		public boolean targetBehindWalls()
		{
			return true;
		}
	};
	
	private final TargetSettings targetSettingsKeep = new TargetSettings()
	{
		@Override
		public boolean targetFriends()
		{
			return true;
		}
		
		@Override
		public boolean targetBehindWalls()
		{
			return true;
		}
		
		@Override
		public boolean targetInvisiblePlayers()
		{
			return true;
		}
		
		@Override
		public boolean targetInvisibleMobs()
		{
			return true;
		}
	};
	
	public RemoteViewMod()
	{
		super("RemoteView", "Allows you to see the world as someone else.\n"
			+ "Use the .rv command to make it target a specific entity.");
		setCategory(Category.RENDER);
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.commands.rvCmd};
	}
	
	@Override
	public void onEnable()
	{
		// find entity if not already set
		if(entity == null)
		{
			entity = EntityUtils.getClosestEntity(targetSettingsFind);
			
			// check if entity was found
			if(entity == null)
			{
				ChatUtils.message("There is no nearby entity.");
				setEnabled(false);
				return;
			}
		}
		
		// save old data
		wasInvisible = entity.isInvisibleToPlayer(WMinecraft.getPlayer());
		
		// enable NoClip
		WMinecraft.getPlayer().noClip = true;
		
		// spawn fake player
		fakePlayer = new EntityFakePlayer();
		
		// success message
		ChatUtils.message("Now viewing " + entity.getName() + ".");
		
		// add listener
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		// remove listener
		wurst.events.remove(UpdateListener.class, this);
		
		// reset entity
		if(entity != null)
		{
			ChatUtils.message("No longer viewing " + entity.getName() + ".");
			entity.setInvisible(wasInvisible);
			entity = null;
		}
		
		// disable NoClip
		WMinecraft.getPlayer().noClip = false;
		
		// remove fake player
		fakePlayer.resetPlayerPosition();
		fakePlayer.despawn();
	}
	
	public void onToggledByCommand(String viewName)
	{
		// set entity
		if(!isEnabled() && viewName != null && !viewName.isEmpty())
			entity = EntityUtils.getClosestEntityWithName(viewName,
				targetSettingsFind);
		
		// toggle RemoteView
		toggle();
	}
	
	@Override
	public void onUpdate()
	{
		// validate entity
		if(!EntityUtils.isCorrectEntity(entity, targetSettingsKeep))
		{
			setEnabled(false);
			return;
		}
		
		// update position, rotation, etc.
		WMinecraft.getPlayer().copyLocationAndAnglesFrom(entity);
		WMinecraft.getPlayer().motionX = 0;
		WMinecraft.getPlayer().motionY = 0;
		WMinecraft.getPlayer().motionZ = 0;
		
		// set entity invisible
		entity.setInvisible(true);
	}
}
