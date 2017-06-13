/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.render;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import org.lwjgl.opengl.GL11;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.compatibility.WBlock;
import net.wurstclient.compatibility.WItem;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.listeners.RenderListener;
import net.wurstclient.events.listeners.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Feature;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;
import net.wurstclient.utils.EntityUtils;
import net.wurstclient.utils.RenderUtils;

@SearchTags({"ChestFinder", "StorageESP", "chest esp", "chest finder",
	"storage esp"})
@Mod.Bypasses
public final class ChestEspMod extends Mod
	implements UpdateListener, RenderListener
{
	private final ArrayList<AxisAlignedBB> basicNew = new ArrayList<>();
	private final ArrayList<AxisAlignedBB> basicEmpty = new ArrayList<>();
	private final ArrayList<AxisAlignedBB> basicNotEmpty = new ArrayList<>();
	
	private final ArrayList<AxisAlignedBB> trappedNew = new ArrayList<>();
	private final ArrayList<AxisAlignedBB> trappedEmpty = new ArrayList<>();
	private final ArrayList<AxisAlignedBB> trappedNotEmpty = new ArrayList<>();
	
	private final ArrayList<AxisAlignedBB> specialEnder = new ArrayList<>();
	private final ArrayList<Entity> specialCart = new ArrayList<>();
	
	private int totalChests;
	
	private TileEntityChest openChest;
	private final LinkedHashSet<BlockPos> emptyChests = new LinkedHashSet<>();
	private final LinkedHashSet<BlockPos> nonEmptyChests =
		new LinkedHashSet<>();
	
	public ChestEspMod()
	{
		super("ChestESP", "Allows you to see chests through walls.\n"
			+ "Works with normal chests, trapped chests, ender chests and minecart chests.\n"
			+ "For normal and trapped chests, ChestESP will remember which ones you have already\n"
			+ "opened and remind you whether or not they are empty by slightly altering their overlay.");
		setCategory(Category.RENDER);
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.mods.autoStealMod, wurst.mods.itemEspMod};
	}
	
	@Override
	public String getRenderName()
	{
		if(totalChests == 1)
			return getName() + " [1 chest]";
		else
			return getName() + " [" + totalChests + " chests]";
	}
	
	@Override
	public void onEnable()
	{
		emptyChests.clear();
		nonEmptyChests.clear();
		
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
	public void onUpdate()
	{
		// clear lists
		basicNew.clear();
		basicEmpty.clear();
		basicNotEmpty.clear();
		trappedNew.clear();
		trappedEmpty.clear();
		trappedNotEmpty.clear();
		specialEnder.clear();
		specialCart.clear();
		
		for(TileEntity tileEntity : WMinecraft.getWorld().loadedTileEntityList)
		{
			// normal chests
			if(tileEntity instanceof TileEntityChest)
			{
				TileEntityChest chest = (TileEntityChest)tileEntity;
				
				// ignore other block in double chest
				if(chest.adjacentChestXPos != null
					|| chest.adjacentChestZPos != null)
					continue;
				
				// get hitbox
				AxisAlignedBB bb = WBlock.getBoundingBox(chest.getPos());
				if(bb == null)
					continue;
				
				// larger box for double chest
				if(chest.adjacentChestXNeg != null)
					bb = bb.union(WBlock
						.getBoundingBox(chest.adjacentChestXNeg.getPos()));
				else if(chest.adjacentChestZNeg != null)
					bb = bb.union(WBlock
						.getBoundingBox(chest.adjacentChestZNeg.getPos()));
				
				boolean trapped = EntityUtils.isTrappedChest(chest);
				
				// add to appropriate list
				if(emptyChests.contains(chest.getPos()))
				{
					if(trapped)
						trappedEmpty.add(bb);
					else
						basicEmpty.add(bb);
					
				}else if(nonEmptyChests.contains(chest.getPos()))
				{
					if(trapped)
						trappedNotEmpty.add(bb);
					else
						basicNotEmpty.add(bb);
					
				}else if(trapped)
					trappedNew.add(bb);
				else
					basicNew.add(bb);
				
				continue;
			}
			
			// ender chests
			if(tileEntity instanceof TileEntityEnderChest)
			{
				AxisAlignedBB bb = WBlock.getBoundingBox(
					((TileEntityEnderChest)tileEntity).getPos());
				specialEnder.add(bb);
			}
		}
		
		// minecarts
		for(Entity entity : WMinecraft.getWorld().loadedEntityList)
			if(entity instanceof EntityMinecartChest)
				specialCart.add(entity);
			
		// chest counter
		totalChests = basicNew.size() + basicEmpty.size() + basicNotEmpty.size()
			+ trappedNew.size() + trappedEmpty.size() + trappedNotEmpty.size()
			+ specialEnder.size() + specialCart.size();
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
		
		// TODO: interpolation for minecarts
		
		GL11.glColor4f(0, 1, 0, 0.25F);
		basicNew.forEach((bb) -> RenderUtils.drawSolidBox(bb));
		specialCart.forEach((e) -> RenderUtils.drawSolidBox(e.boundingBox));
		
		GL11.glColor4f(0, 1, 0, 0.5F);
		basicNew.forEach((bb) -> RenderUtils.drawOutlinedBox(bb));
		basicEmpty.forEach((bb) -> RenderUtils.drawOutlinedBox(bb));
		basicNotEmpty.forEach((bb) -> RenderUtils.drawOutlinedBox(bb));
		basicNotEmpty.forEach((bb) -> RenderUtils.drawCrossBox(bb));
		specialCart.forEach((e) -> RenderUtils.drawOutlinedBox(e.boundingBox));
		
		GL11.glColor4f(1, 0.5F, 0, 0.25F);
		trappedNew.forEach((bb) -> RenderUtils.drawSolidBox(bb));
		
		GL11.glColor4f(1, 0.5F, 0, 0.5F);
		trappedNew.forEach((bb) -> RenderUtils.drawOutlinedBox(bb));
		trappedEmpty.forEach((bb) -> RenderUtils.drawOutlinedBox(bb));
		trappedNotEmpty.forEach((bb) -> RenderUtils.drawOutlinedBox(bb));
		trappedNotEmpty.forEach((bb) -> RenderUtils.drawCrossBox(bb));
		
		GL11.glColor4f(0, 1, 1, 0.25F);
		specialEnder.forEach((bb) -> RenderUtils.drawSolidBox(bb));
		
		GL11.glColor4f(0, 1, 1, 0.5F);
		specialEnder.forEach((bb) -> RenderUtils.drawOutlinedBox(bb));
		
		GL11.glPopMatrix();
		
		// GL resets
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
	}
	
	public void openChest(BlockPos pos)
	{
		TileEntity tileEntity = WMinecraft.getWorld().getTileEntity(pos);
		if(tileEntity instanceof TileEntityChest)
		{
			openChest = (TileEntityChest)tileEntity;
			if(openChest.adjacentChestXPos != null)
				openChest = openChest.adjacentChestXPos;
			if(openChest.adjacentChestZPos != null)
				openChest = openChest.adjacentChestZPos;
		}
	}
	
	public void closeChest(Container chest)
	{
		if(openChest == null)
			return;
		
		boolean empty = true;
		for(int i = 0; i < chest.inventorySlots.size() - 36; i++)
			if(!WItem.isNull(chest.inventorySlots.get(i).getStack()))
			{
				empty = false;
				break;
			}
		
		BlockPos pos = openChest.getPos();
		if(empty)
		{
			if(!emptyChests.contains(pos))
				emptyChests.add(pos);
			
			nonEmptyChests.remove(pos);
		}else
		{
			if(!nonEmptyChests.contains(pos))
				nonEmptyChests.add(pos);
			
			emptyChests.remove(pos);
		}
		
		openChest = null;
	}
}
