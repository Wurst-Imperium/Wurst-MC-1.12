/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.blocks;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.compatibility.WBlock;
import net.wurstclient.compatibility.WItem;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.compatibility.WPlayer;
import net.wurstclient.events.RenderListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Feature;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;
import net.wurstclient.features.special_features.YesCheatSpf.Profile;
import net.wurstclient.settings.ModeSetting;
import net.wurstclient.utils.BlockUtils;
import net.wurstclient.utils.RenderUtils;

@SearchTags({"build random"})
@Mod.Bypasses
public final class BuildRandomMod extends Mod
	implements UpdateListener, RenderListener
{
	private final Random random = new Random();
	private final ModeSetting mode = new ModeSetting("Mode",
		"§lFast§r mode can place blocks behind other blocks.\n"
			+ "§lLegit§r mode can bypass NoCheat+.",
		new String[]{"Fast", "Legit"}, 1);
	
	private BlockPos lastPos;
	
	public BuildRandomMod()
	{
		super("BuildRandom", "Randomly places blocks around you.\n"
			+ "Tip: Using this mod in combination with FastPlace will make it faster.");
		setCategory(Category.BLOCKS);
	}
	
	@Override
	public void initSettings()
	{
		addSetting(mode);
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.mods.fastPlaceMod, wurst.mods.autoSwitchMod,
			wurst.mods.autoBuildMod};
	}
	
	@Override
	public void onEnable()
	{
		wurst.events.add(UpdateListener.class, this);
		wurst.events.add(RenderListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		lastPos = null;
		wurst.events.remove(UpdateListener.class, this);
		wurst.events.remove(RenderListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		lastPos = null;
		
		if(wurst.mods.freecamMod.isActive()
			|| wurst.mods.remoteViewMod.isActive())
			return;
		
		// check timer
		if(mc.rightClickDelayTimer > 0 && !wurst.mods.fastPlaceMod.isActive())
			return;
		
		// check held item
		ItemStack stack = WMinecraft.getPlayer().inventory.getCurrentItem();
		if(WItem.isNullOrEmpty(stack)
			|| !(stack.getItem() instanceof ItemBlock))
			return;
		
		// set mode & range
		boolean legitMode = mode.getSelected() == 1;
		int range = legitMode ? 5 : 6;
		int bound = range * 2 + 1;
		
		BlockPos pos;
		int attempts = 0;
		
		do
		{
			// generate random position
			pos = new BlockPos(WMinecraft.getPlayer()).add(
				random.nextInt(bound) - range, random.nextInt(bound) - range,
				random.nextInt(bound) - range);
			attempts++;
		}while(attempts < 128 && !tryToPlaceBlock(legitMode, pos));
	}
	
	@Override
	public void onRender(float partialTicks)
	{
		if(lastPos == null)
			return;
		
		// GL settings
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glLineWidth(2);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		GL11.glPushMatrix();
		GL11.glTranslated(-mc.getRenderManager().renderPosX,
			-mc.getRenderManager().renderPosY,
			-mc.getRenderManager().renderPosZ);
		
		// set position
		GL11.glTranslated(lastPos.getX(), lastPos.getY(), lastPos.getZ());
		
		// get color
		float red = partialTicks * 2F;
		float green = 2 - red;
		
		// draw box
		GL11.glColor4f(red, green, 0, 0.25F);
		RenderUtils.drawSolidBox();
		GL11.glColor4f(red, green, 0, 0.5F);
		RenderUtils.drawOutlinedBox();
		
		GL11.glPopMatrix();
		
		// GL resets
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
	}
	
	@Override
	public void onYesCheatUpdate(Profile profile)
	{
		switch(profile)
		{
			case OFF:
			case MINEPLEX:
			mode.unlock();
			break;
			
			default:
			mode.lock(1);
			break;
		}
	}
	
	private boolean tryToPlaceBlock(boolean legitMode, BlockPos pos)
	{
		if(!WBlock.getMaterial(pos).isReplaceable())
			return false;
		
		if(legitMode)
		{
			if(!BlockUtils.placeBlockLegit(pos))
				return false;
			
			mc.rightClickDelayTimer = 4;
		}else
		{
			if(!BlockUtils.placeBlockSimple_old(pos))
				return false;
			
			WPlayer.swingArmClient();
			mc.rightClickDelayTimer = 4;
		}
		
		lastPos = pos;
		return true;
	}
}
