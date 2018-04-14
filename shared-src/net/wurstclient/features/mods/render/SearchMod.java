/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.render;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.compatibility.WMath;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.RenderListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Mod;
import net.wurstclient.utils.ChatUtils;
import net.wurstclient.utils.RenderUtils;

@Mod.Bypasses
public final class SearchMod extends Mod
	implements UpdateListener, RenderListener
{
	private ArrayList<BlockPos> matchingBlocks = new ArrayList<>();
	private int range = 50;
	private int maxBlocks = 1000;
	public boolean notify = true;
	
	public SearchMod()
	{
		super("Search", "Helps you to find specific blocks.\n"
			+ "Use §l.search id <block_id>§r or §l.search name <block_name>§r to specify the block to search\n"
			+ "for.");
		setCategory(Category.RENDER);
	}
	
	@Override
	public String getRenderName()
	{
		return getName() + " [" + wurst.options.searchID + "]";
	}
	
	@Override
	public void onEnable()
	{
		notify = true;
		wurst.events.add(UpdateListener.class, this);
		wurst.events.add(RenderListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
		wurst.events.remove(RenderListener.class, this);
	}
	
	@Override
	public void onRender(float partialTicks)
	{
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
		
		// generate rainbow color
		float x = System.currentTimeMillis() % 2000 / 1000F;
		float red = 0.5F + 0.5F * WMath.sin(x * (float)Math.PI);
		float green = 0.5F + 0.5F * WMath.sin((x + 4F / 3F) * (float)Math.PI);
		float blue = 0.5F + 0.5F * WMath.sin((x + 8F / 3F) * (float)Math.PI);
		
		// render boxes
		for(BlockPos pos : matchingBlocks)
		{
			GL11.glPushMatrix();
			GL11.glTranslated(pos.getX(), pos.getY(), pos.getZ());
			
			GL11.glColor4f(red, green, blue, 0.5F);
			RenderUtils.drawOutlinedBox();
			
			GL11.glColor4f(red, green, blue, 0.25F);
			RenderUtils.drawSolidBox();
			
			GL11.glPopMatrix();
		}
		
		GL11.glPopMatrix();
		
		// GL resets
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
	}
	
	@Override
	public void onUpdate()
	{
		updateMS();
		if(hasTimePassedM(3000))
		{
			matchingBlocks.clear();
			for(int y = range; y >= -range; y--)
			{
				for(int x = range; x >= -range; x--)
				{
					for(int z = range; z >= -range; z--)
					{
						int posX = (int)(WMinecraft.getPlayer().posX + x);
						int posY = (int)(WMinecraft.getPlayer().posY + y);
						int posZ = (int)(WMinecraft.getPlayer().posZ + z);
						BlockPos pos = new BlockPos(posX, posY, posZ);
						if(Block.getIdFromBlock(
							WMinecraft.getWorld().getBlockState(pos)
								.getBlock()) == wurst.options.searchID)
							matchingBlocks.add(pos);
						if(matchingBlocks.size() >= maxBlocks)
							break;
					}
					if(matchingBlocks.size() >= maxBlocks)
						break;
				}
				if(matchingBlocks.size() >= maxBlocks)
					break;
			}
			if(matchingBlocks.size() >= maxBlocks && notify)
			{
				ChatUtils.warning(getName() + " found §lA LOT§r of blocks.");
				ChatUtils.message("To prevent lag, it will only show the first "
					+ maxBlocks + " blocks.");
				notify = false;
			}else if(matchingBlocks.size() < maxBlocks)
				notify = true;
			updateLastMS();
		}
	}
}
