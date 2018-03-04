/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.render;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.compatibility.WBlock;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.RenderListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;
import net.wurstclient.utils.ChatUtils;

@SearchTags({"base finder", "factions"})
@Mod.Bypasses
public final class BaseFinderMod extends Mod
	implements UpdateListener, RenderListener
{
	private static final List<Block> NATURAL_BLOCKS = Arrays.<Block> asList(
		Blocks.AIR, Blocks.STONE, Blocks.DIRT, Blocks.GRASS, Blocks.GRAVEL,
		Blocks.SAND, Blocks.CLAY, Blocks.SANDSTONE, Blocks.FLOWING_WATER,
		Blocks.WATER, Blocks.FLOWING_LAVA, Blocks.LAVA, Blocks.LOG, Blocks.LOG2,
		Blocks.LEAVES, Blocks.LEAVES2, Blocks.DEADBUSH, Blocks.IRON_ORE,
		Blocks.COAL_ORE, Blocks.GOLD_ORE, Blocks.DIAMOND_ORE,
		Blocks.EMERALD_ORE, Blocks.REDSTONE_ORE, Blocks.LAPIS_ORE,
		Blocks.BEDROCK, Blocks.MOB_SPAWNER, Blocks.MOSSY_COBBLESTONE,
		Blocks.TALLGRASS, Blocks.YELLOW_FLOWER, Blocks.RED_FLOWER, Blocks.WEB,
		Blocks.RED_MUSHROOM, Blocks.BROWN_MUSHROOM, Blocks.SNOW_LAYER,
		Blocks.VINE, Blocks.WATERLILY, Blocks.DOUBLE_PLANT,
		Blocks.HARDENED_CLAY, Blocks.RED_SANDSTONE, Blocks.ICE,
		Blocks.QUARTZ_ORE, Blocks.OBSIDIAN, Blocks.MONSTER_EGG,
		Blocks.RED_MUSHROOM_BLOCK, Blocks.BROWN_MUSHROOM_BLOCK);
	
	private final HashSet<BlockPos> matchingBlocks = new HashSet<>();
	private final ArrayList<int[]> vertices = new ArrayList<>();
	
	private int messageTimer = 0;
	private int counter;
	
	public BaseFinderMod()
	{
		super("BaseFinder",
			"Finds player bases by searching for man-made blocks.\n"
				+ "The blocks that it finds will be highlighted in red.\n"
				+ "Good for finding faction bases.");
		setCategory(Category.RENDER);
	}
	
	@Override
	public String getRenderName()
	{
		String name = getName() + " [";
		
		// counter
		if(counter >= 10000)
			name += "10000+ blocks";
		else if(counter == 1)
			name += "1 block";
		else if(counter == 0)
			name += "nothing";
		else
			name += counter + " blocks";
		
		name += " found]";
		return name;
	}
	
	@Override
	public void onEnable()
	{
		// reset timer
		messageTimer = 0;
		
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
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glColor4f(1F, 0F, 0F, 0.15F);
		
		GL11.glPushMatrix();
		GL11.glTranslated(-mc.getRenderManager().renderPosX,
			-mc.getRenderManager().renderPosY,
			-mc.getRenderManager().renderPosZ);
		
		// vertices
		GL11.glBegin(GL11.GL_QUADS);
		{
			for(int[] vertex : vertices)
				GL11.glVertex3d(vertex[0], vertex[1], vertex[2]);
		}
		GL11.glEnd();
		
		GL11.glPopMatrix();
		
		// GL resets
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	@Override
	public void onUpdate()
	{
		int modulo = WMinecraft.getPlayer().ticksExisted % 64;
		
		// reset matching blocks
		if(modulo == 0)
			matchingBlocks.clear();
		
		int startY = 255 - modulo * 4;
		int endY = startY - 4;
		
		BlockPos playerPos = new BlockPos(WMinecraft.getPlayer().posX, 0,
			WMinecraft.getPlayer().posZ);
		
		// search matching blocks
		loop: for(int y = startY; y > endY; y--)
			for(int x = 64; x > -64; x--)
				for(int z = 64; z > -64; z--)
				{
					if(matchingBlocks.size() >= 10000)
						break loop;
					
					BlockPos pos = playerPos.add(x, y, z);
					
					if(NATURAL_BLOCKS.contains(WBlock.getBlock(pos)))
						continue;
					
					matchingBlocks.add(pos);
				}
			
		if(modulo != 63)
			return;
		
		// update timer
		if(matchingBlocks.size() < 10000)
			messageTimer--;
		else
		{
			// show message
			if(messageTimer <= 0)
			{
				ChatUtils.warning("BaseFinder found §lA LOT§r of blocks.");
				ChatUtils.message(
					"To prevent lag, it will only show the first 10000 blocks.");
			}
			
			// reset timer
			messageTimer = 3;
		}
		
		// update counter
		counter = matchingBlocks.size();
		
		// calculate vertices
		vertices.clear();
		for(BlockPos pos : matchingBlocks)
		{
			if(!matchingBlocks.contains(pos.down()))
			{
				addVertex(pos, 0, 0, 0);
				addVertex(pos, 1, 0, 0);
				addVertex(pos, 1, 0, 1);
				addVertex(pos, 0, 0, 1);
			}
			
			if(!matchingBlocks.contains(pos.up()))
			{
				addVertex(pos, 0, 1, 0);
				addVertex(pos, 0, 1, 1);
				addVertex(pos, 1, 1, 1);
				addVertex(pos, 1, 1, 0);
			}
			
			if(!matchingBlocks.contains(pos.north()))
			{
				addVertex(pos, 0, 0, 0);
				addVertex(pos, 0, 1, 0);
				addVertex(pos, 1, 1, 0);
				addVertex(pos, 1, 0, 0);
			}
			
			if(!matchingBlocks.contains(pos.east()))
			{
				addVertex(pos, 1, 0, 0);
				addVertex(pos, 1, 1, 0);
				addVertex(pos, 1, 1, 1);
				addVertex(pos, 1, 0, 1);
			}
			
			if(!matchingBlocks.contains(pos.south()))
			{
				addVertex(pos, 0, 0, 1);
				addVertex(pos, 1, 0, 1);
				addVertex(pos, 1, 1, 1);
				addVertex(pos, 0, 1, 1);
			}
			
			if(!matchingBlocks.contains(pos.west()))
			{
				addVertex(pos, 0, 0, 0);
				addVertex(pos, 0, 0, 1);
				addVertex(pos, 0, 1, 1);
				addVertex(pos, 0, 1, 0);
			}
		}
	}
	
	private void addVertex(BlockPos pos, int x, int y, int z)
	{
		vertices.add(new int[]{pos.getX() + x, pos.getY() + y, pos.getZ() + z});
	}
}
