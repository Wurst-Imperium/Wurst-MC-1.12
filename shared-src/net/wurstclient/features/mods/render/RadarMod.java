/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.render;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.wurstclient.clickgui.Radar;
import net.wurstclient.clickgui.Window;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;
import net.wurstclient.utils.EntityFakePlayer;

@SearchTags({"MiniMap", "mini map"})
@Mod.Bypasses
public final class RadarMod extends Mod implements UpdateListener
{
	private final Window window;
	private final ArrayList<Entity> entities = new ArrayList<>();
	
	private final SliderSetting radius = new SliderSetting("Radius",
		"Radius in blocks.", 100, 1, 100, 1, ValueDisplay.INTEGER);
	private final CheckboxSetting rotate =
		new CheckboxSetting("Rotate with player", true);
	
	private final CheckboxSetting filterPlayers = new CheckboxSetting(
		"Filter players", "Won't show other players.", false);
	private final CheckboxSetting filterSleeping = new CheckboxSetting(
		"Filter sleeping", "Won't show sleeping players.", false);
	private final CheckboxSetting filterMonsters = new CheckboxSetting(
		"Filter monsters", "Won't show zombies, creepers, etc.", false);
	private final CheckboxSetting filterAnimals = new CheckboxSetting(
		"Filter animals", "Won't show pigs, cows, etc.", false);
	private final CheckboxSetting filterInvisible = new CheckboxSetting(
		"Filter invisible", "Won't show invisible entities.", false);
	
	public RadarMod()
	{
		super("Radar",
			"Shows the location of nearby entities.\n" + ChatFormatting.RED
				+ "red" + ChatFormatting.RESET + " - players\n"
				+ ChatFormatting.GOLD + "orange" + ChatFormatting.RESET
				+ " - monsters\n" + ChatFormatting.GREEN + "green"
				+ ChatFormatting.RESET + " - animals\n" + ChatFormatting.GRAY
				+ "gray" + ChatFormatting.RESET + " - others\n");
		setCategory(Category.RENDER);
		addSetting(radius);
		addSetting(rotate);
		addSetting(filterPlayers);
		addSetting(filterSleeping);
		addSetting(filterMonsters);
		addSetting(filterAnimals);
		addSetting(filterInvisible);
		
		window = new Window("Radar");
		window.setPinned(true);
		window.setInvisible(true);
		window.add(new Radar(this));
	}
	
	@Override
	public void onEnable()
	{
		wurst.events.add(UpdateListener.class, this);
		window.setInvisible(false);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
		window.setInvisible(true);
	}
	
	@Override
	public void onUpdate()
	{
		EntityPlayerSP player = WMinecraft.getPlayer();
		World world = WMinecraft.getWorld();
		
		entities.clear();
		Stream<Entity> stream = world.loadedEntityList.parallelStream()
			.filter(e -> !e.isDead && e != player)
			.filter(e -> !(e instanceof EntityFakePlayer))
			.filter(e -> e instanceof EntityLivingBase)
			.filter(e -> ((EntityLivingBase)e).getHealth() > 0);
		
		if(filterPlayers.isChecked())
			stream = stream.filter(e -> !(e instanceof EntityPlayer));
		
		if(filterSleeping.isChecked())
			stream = stream.filter(e -> !(e instanceof EntityPlayer
				&& ((EntityPlayer)e).isPlayerSleeping()));
		
		if(filterMonsters.isChecked())
			stream = stream.filter(e -> !(e instanceof IMob));
		
		if(filterAnimals.isChecked())
			stream = stream.filter(e -> !(e instanceof EntityAnimal
				|| e instanceof EntityAmbientCreature
				|| e instanceof EntityWaterMob));
		
		if(filterInvisible.isChecked())
			stream = stream.filter(e -> !e.isInvisible());
		
		entities.addAll(stream.collect(Collectors.toList()));
	}
	
	public Window getWindow()
	{
		return window;
	}
	
	public Iterable<Entity> getEntities()
	{
		return Collections.unmodifiableList(entities);
	}
	
	public double getRadius()
	{
		return radius.getValue();
	}
	
	public boolean isRotateEnabled()
	{
		return rotate.isChecked();
	}
}
