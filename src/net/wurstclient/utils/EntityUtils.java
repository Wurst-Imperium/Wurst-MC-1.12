/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.utils;

import java.util.ArrayList;
import java.util.UUID;

import net.minecraft.block.BlockChest;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumHand;
import net.wurstclient.WurstClient;
import net.wurstclient.compatibility.WConnection;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.compatibility.WPlayer;

public class EntityUtils
{
	private static final WurstClient wurst = WurstClient.INSTANCE;
	private static final Minecraft mc = Minecraft.getMinecraft();
	
	public static final TargetSettings DEFAULT_SETTINGS = new TargetSettings();
	
	public static void prepareAttack()
	{
		// AutoSword
		wurst.mods.autoSwordMod.setSlot();
		
		// Criticals
		wurst.mods.criticalsMod.doCritical();
	}
	
	public static void attackEntity(Entity entity)
	{
		mc.playerController.attackEntity(WMinecraft.getPlayer(), entity);
		WPlayer.swingArmClient();
	}
	
	public static void sendAttackPacket(Entity entity)
	{
		WConnection
			.sendPacket(new CPacketUseEntity(entity, EnumHand.MAIN_HAND));
	}
	
	public static boolean isTrappedChest(TileEntityChest chest)
	{
		return chest.getChestType() == BlockChest.Type.TRAP;
	}
	
	public static boolean isCorrectEntity(Entity en, TargetSettings settings)
	{
		// non-entities
		if(en == null)
			return false;
		
		// dead entities
		if(en instanceof EntityLivingBase && (((EntityLivingBase)en).isDead
			|| ((EntityLivingBase)en).getHealth() <= 0))
			return false;
		
		// entities outside the range
		if(WMinecraft.getPlayer().getDistanceToEntity(en) > settings.getRange())
			return false;
		
		// entities outside the FOV
		if(settings.getFOV() < 360F && RotationUtils.getAngleToClientRotation(
			en.boundingBox.getCenter()) > settings.getFOV() / 2F)
			return false;
		
		// entities behind walls
		if(!settings.targetBehindWalls()
			&& !WMinecraft.getPlayer().canEntityBeSeen(en))
			return false;
		
		// friends
		if(!settings.targetFriends()
			&& WurstClient.INSTANCE.friends.contains(en.getName()))
			return false;
		
		// players
		if(en instanceof EntityPlayer)
		{
			// normal players
			if(!settings.targetPlayers())
			{
				if(!((EntityPlayer)en).isPlayerSleeping()
					&& !((EntityPlayer)en).isInvisible())
					return false;
				
				// sleeping players
			}else if(!settings.targetSleepingPlayers())
			{
				if(((EntityPlayer)en).isPlayerSleeping())
					return false;
				
				// invisible players
			}else if(!settings.targetInvisiblePlayers())
				if(((EntityPlayer)en).isInvisible())
					return false;
				
			// team players
			if(settings.targetTeams() && !checkName(
				((EntityPlayer)en).getDisplayName().getFormattedText(),
				settings.getTeamColors()))
				return false;
			
			// the user
			if(en == WMinecraft.getPlayer())
				return false;
			
			// Freecam entity
			if(((EntityPlayer)en).getName()
				.equals(WMinecraft.getPlayer().getName()))
				return false;
			
			// mobs
		}else if(en instanceof EntityLiving)
		{
			// invisible mobs
			if(((EntityLiving)en).isInvisible())
			{
				if(!settings.targetInvisibleMobs())
					return false;
				
				// animals
			}else if(en instanceof EntityAgeable
				|| en instanceof EntityAmbientCreature
				|| en instanceof EntityWaterMob)
			{
				if(!settings.targetAnimals())
					return false;
				
				// monsters
			}else if(en instanceof EntityMob || en instanceof EntitySlime
				|| en instanceof EntityFlying)
			{
				if(!settings.targetMonsters())
					return false;
				
				// golems
			}else if(en instanceof EntityGolem)
			{
				if(!settings.targetGolems())
					return false;
				
				// other mobs
			}else
				return false;
			
			// team mobs
			if(settings.targetTeams() && ((EntityLiving)en).hasCustomName()
				&& !checkName(((EntityLiving)en).getCustomNameTag(),
					settings.getTeamColors()))
				return false;
			
			// other entities
		}else
			return false;
		
		return true;
	}
	
	private static boolean checkName(String name, boolean[] teamColors)
	{
		// check colors
		String[] colors = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
			"a", "b", "c", "d", "e", "f"};
		boolean hasKnownColor = false;
		for(int i = 0; i < 16; i++)
			if(name.contains("§" + colors[i]))
			{
				hasKnownColor = true;
				if(teamColors[i])
					return true;
			}
		
		// no known color => white
		return !hasKnownColor && teamColors[15];
	}
	
	public static ArrayList<Entity> getValidEntities(TargetSettings settings)
	{
		ArrayList<Entity> validEntities = new ArrayList<>();
		
		for(Entity entity : WMinecraft.getWorld().loadedEntityList)
		{
			if(isCorrectEntity(entity, settings))
				validEntities.add(entity);
			
			if(validEntities.size() >= 64)
				break;
		}
		
		return validEntities;
	}
	
	public static Entity getClosestEntity(TargetSettings settings)
	{
		Entity closestEntity = null;
		
		for(Entity entity : WMinecraft.getWorld().loadedEntityList)
			if(isCorrectEntity(entity, settings)
				&& (closestEntity == null || WMinecraft.getPlayer()
					.getDistanceToEntity(entity) < WMinecraft.getPlayer()
						.getDistanceToEntity(closestEntity)))
				closestEntity = entity;
			
		return closestEntity;
	}
	
	public static Entity getBestEntityToAttack(TargetSettings settings)
	{
		Entity bestEntity = null;
		float bestAngle = Float.POSITIVE_INFINITY;
		
		for(Entity entity : WMinecraft.getWorld().loadedEntityList)
		{
			if(!isCorrectEntity(entity, settings))
				continue;
			
			float angle = RotationUtils
				.getAngleToServerRotation(entity.boundingBox.getCenter());
			
			if(angle < bestAngle)
			{
				bestEntity = entity;
				bestAngle = angle;
			}
		}
		
		return bestEntity;
	}
	
	public static Entity getClosestEntityOtherThan(Entity otherEntity,
		TargetSettings settings)
	{
		Entity closestEnemy = null;
		
		for(Entity entity : WMinecraft.getWorld().loadedEntityList)
			if(isCorrectEntity(entity, settings) && entity != otherEntity
				&& (closestEnemy == null || WMinecraft.getPlayer()
					.getDistanceToEntity(entity) < WMinecraft.getPlayer()
						.getDistanceToEntity(closestEnemy)))
				closestEnemy = entity;
			
		return closestEnemy;
	}
	
	public static Entity getEntityWithName(String name, TargetSettings settings)
	{
		for(Entity entity : WMinecraft.getWorld().loadedEntityList)
			if(isCorrectEntity(entity, settings)
				&& entity.getName().equalsIgnoreCase(name))
				return entity;
			
		return null;
	}
	
	public static Entity getEntityWithId(UUID id, TargetSettings settings)
	{
		for(Entity entity : WMinecraft.getWorld().loadedEntityList)
			if(isCorrectEntity(entity, settings)
				&& entity.getUniqueID().equals(id))
				return entity;
			
		return null;
	}
	
	public static class TargetSettings
	{
		public boolean targetFriends()
		{
			return false;
		}
		
		public boolean targetBehindWalls()
		{
			return false;
		}
		
		public float getRange()
		{
			return Float.POSITIVE_INFINITY;
		}
		
		public float getFOV()
		{
			return 360F;
		}
		
		public boolean targetPlayers()
		{
			return WurstClient.INSTANCE.special.targetSpf.players.isChecked();
		}
		
		public boolean targetAnimals()
		{
			return WurstClient.INSTANCE.special.targetSpf.animals.isChecked();
		}
		
		public boolean targetMonsters()
		{
			return WurstClient.INSTANCE.special.targetSpf.monsters.isChecked();
		}
		
		public boolean targetGolems()
		{
			return WurstClient.INSTANCE.special.targetSpf.golems.isChecked();
		}
		
		public boolean targetSleepingPlayers()
		{
			return WurstClient.INSTANCE.special.targetSpf.sleepingPlayers
				.isChecked();
		}
		
		public boolean targetInvisiblePlayers()
		{
			return WurstClient.INSTANCE.special.targetSpf.invisiblePlayers
				.isChecked();
		}
		
		public boolean targetInvisibleMobs()
		{
			return WurstClient.INSTANCE.special.targetSpf.invisibleMobs
				.isChecked();
		}
		
		public boolean targetTeams()
		{
			return WurstClient.INSTANCE.special.targetSpf.teams.isChecked();
		}
		
		public boolean[] getTeamColors()
		{
			return WurstClient.INSTANCE.special.targetSpf.teamColors
				.getSelected();
		}
	}
}
