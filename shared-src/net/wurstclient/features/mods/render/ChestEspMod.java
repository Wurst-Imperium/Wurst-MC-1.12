/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
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
import net.minecraft.util.math.Vec3d;
import net.wurstclient.compatibility.WBlock;
import net.wurstclient.compatibility.WItem;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.compatibility.WTileEntity;
import net.wurstclient.events.CameraTransformViewBobbingListener;
import net.wurstclient.events.RenderListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Feature;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.utils.RenderUtils;
import net.wurstclient.utils.RotationUtils;

@SearchTags({"ChestFinder", "StorageESP", "ChestTracers", "chest esp",
	"chest finder", "storage esp", "chest tracers"})
@Mod.Bypasses
public final class ChestEspMod extends Mod implements UpdateListener,
	CameraTransformViewBobbingListener, RenderListener
{
	private final CheckboxSetting tracers =
		new CheckboxSetting("Tracers", "Draws lines to chests.", false);
	
	private final ArrayList<AxisAlignedBB> basicChests = new ArrayList<>();
	private final ArrayList<AxisAlignedBB> trappedChests = new ArrayList<>();
	private final ArrayList<AxisAlignedBB> enderChests = new ArrayList<>();
	private final ArrayList<Entity> minecarts = new ArrayList<>();
	
	private int solidBox;
	private int outlinedBox;
	private int crossBox;
	
	private int normalChests;
	
	private int chestCounter;
	
	private TileEntityChest openChest;
	private final LinkedHashSet<BlockPos> emptyChests = new LinkedHashSet<>();
	private final LinkedHashSet<BlockPos> nonEmptyChests =
		new LinkedHashSet<>();
	
	public ChestEspMod()
	{
		super("ChestESP",
			"Highlights nearby chests.\n" + "§agreen§r - normal chests\n"
				+ "§6orange§r - trapped chests\n" + "§bcyan§r - ender chests\n"
				+ "[  ] - empty\n" + "[X] - not empty");
		setCategory(Category.RENDER);
		addSetting(tracers);
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.mods.autoStealMod, wurst.mods.itemEspMod};
	}
	
	@Override
	public String getRenderName()
	{
		return getName() + (chestCounter == 1 ? " [1 chest]"
			: " [" + chestCounter + " chests]");
	}
	
	@Override
	public void onEnable()
	{
		wurst.events.add(UpdateListener.class, this);
		wurst.events.add(CameraTransformViewBobbingListener.class, this);
		wurst.events.add(RenderListener.class, this);
		
		emptyChests.clear();
		nonEmptyChests.clear();
		
		solidBox = GL11.glGenLists(1);
		GL11.glNewList(solidBox, GL11.GL_COMPILE);
		RenderUtils.drawSolidBox();
		GL11.glEndList();
		
		outlinedBox = GL11.glGenLists(1);
		GL11.glNewList(outlinedBox, GL11.GL_COMPILE);
		RenderUtils.drawOutlinedBox();
		GL11.glEndList();
		
		crossBox = GL11.glGenLists(1);
		GL11.glNewList(crossBox, GL11.GL_COMPILE);
		RenderUtils.drawCrossBox();
		GL11.glEndList();
		
		normalChests = GL11.glGenLists(1);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
		wurst.events.remove(CameraTransformViewBobbingListener.class, this);
		wurst.events.remove(RenderListener.class, this);
		
		GL11.glDeleteLists(solidBox, 1);
		solidBox = 0;
		
		GL11.glDeleteLists(outlinedBox, 1);
		outlinedBox = 0;
		
		GL11.glDeleteLists(crossBox, 1);
		crossBox = 0;
		
		GL11.glDeleteLists(normalChests, 1);
		normalChests = 0;
	}
	
	@Override
	public void onUpdate()
	{
		ArrayList<AxisAlignedBB> basicNew = new ArrayList<>();
		ArrayList<AxisAlignedBB> basicEmpty = new ArrayList<>();
		ArrayList<AxisAlignedBB> basicNotEmpty = new ArrayList<>();
		ArrayList<AxisAlignedBB> trappedNew = new ArrayList<>();
		ArrayList<AxisAlignedBB> trappedEmpty = new ArrayList<>();
		ArrayList<AxisAlignedBB> trappedNotEmpty = new ArrayList<>();
		enderChests.clear();
		
		for(TileEntity tileEntity : WMinecraft.getWorld().loadedTileEntityList)
			if(tileEntity instanceof TileEntityChest)
			{
				// ignore other block in double chest
				TileEntityChest chest = (TileEntityChest)tileEntity;
				if(chest.adjacentChestXPos != null
					|| chest.adjacentChestZPos != null)
					continue;
				
				// get hitbox
				AxisAlignedBB bb = WBlock.getBoundingBox(chest.getPos());
				if(bb == null)
					continue;
				
				// larger box for double chest
				if(chest.adjacentChestXNeg != null)
				{
					BlockPos pos2 = chest.adjacentChestXNeg.getPos();
					AxisAlignedBB bb2 = WBlock.getBoundingBox(pos2);
					bb = bb.union(bb2);
					
				}else if(chest.adjacentChestZNeg != null)
				{
					BlockPos pos2 = chest.adjacentChestZNeg.getPos();
					AxisAlignedBB bb2 = WBlock.getBoundingBox(pos2);
					bb = bb.union(bb2);
				}
				
				// add to appropriate list
				boolean trapped = WTileEntity.isTrappedChest(chest);
				if(emptyChests.contains(chest.getPos()))
					if(trapped)
						trappedEmpty.add(bb);
					else
						basicEmpty.add(bb);
				else if(nonEmptyChests.contains(chest.getPos()))
					if(trapped)
						trappedNotEmpty.add(bb);
					else
						basicNotEmpty.add(bb);
				else if(trapped)
					trappedNew.add(bb);
				else
					basicNew.add(bb);
				
			}else if(tileEntity instanceof TileEntityEnderChest)
			{
				AxisAlignedBB bb = WBlock.getBoundingBox(
					((TileEntityEnderChest)tileEntity).getPos());
				enderChests.add(bb);
			}
		
		basicChests.clear();
		basicChests.addAll(basicNew);
		basicChests.addAll(basicEmpty);
		basicChests.addAll(basicNotEmpty);
		
		trappedChests.clear();
		trappedChests.addAll(trappedNew);
		trappedChests.addAll(trappedEmpty);
		trappedChests.addAll(trappedNotEmpty);
		
		GL11.glNewList(normalChests, GL11.GL_COMPILE);
		GL11.glColor4f(0, 1, 0, 0.25F);
		renderBoxes(basicNew, solidBox);
		GL11.glColor4f(0, 1, 0, 0.5F);
		renderBoxes(basicNew, outlinedBox);
		renderBoxes(basicEmpty, outlinedBox);
		renderBoxes(basicNotEmpty, outlinedBox);
		renderBoxes(basicNotEmpty, crossBox);
		GL11.glColor4f(1, 0.5F, 0, 0.25F);
		renderBoxes(trappedNew, solidBox);
		GL11.glColor4f(1, 0.5F, 0, 0.5F);
		renderBoxes(trappedNew, outlinedBox);
		renderBoxes(trappedEmpty, outlinedBox);
		renderBoxes(trappedNotEmpty, outlinedBox);
		renderBoxes(trappedNotEmpty, crossBox);
		GL11.glColor4f(0, 1, 1, 0.25F);
		renderBoxes(enderChests, solidBox);
		GL11.glColor4f(0, 1, 1, 0.5F);
		renderBoxes(enderChests, outlinedBox);
		GL11.glEndList();
		
		// minecarts
		minecarts.clear();
		for(Entity entity : WMinecraft.getWorld().loadedEntityList)
			if(entity instanceof EntityMinecartChest)
				minecarts.add(entity);
			
		// chest counter
		chestCounter = basicNew.size() + basicEmpty.size()
			+ basicNotEmpty.size() + trappedNew.size() + trappedEmpty.size()
			+ trappedNotEmpty.size() + enderChests.size() + minecarts.size();
	}
	
	@Override
	public void onCameraTransformViewBobbing(
		CameraTransformViewBobbingEvent event)
	{
		if(tracers.isChecked())
			event.cancel();
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
		
		// minecart interpolation
		ArrayList<AxisAlignedBB> minecartBoxes =
			new ArrayList<>(minecarts.size());
		minecarts.forEach(e -> {
			double offsetX = -(e.posX - e.lastTickPosX)
				+ (e.posX - e.lastTickPosX) * partialTicks;
			double offsetY = -(e.posY - e.lastTickPosY)
				+ (e.posY - e.lastTickPosY) * partialTicks;
			double offsetZ = -(e.posZ - e.lastTickPosZ)
				+ (e.posZ - e.lastTickPosZ) * partialTicks;
			minecartBoxes.add(e.boundingBox.offset(offsetX, offsetY, offsetZ));
		});
		
		GL11.glCallList(normalChests);
		
		GL11.glColor4f(0, 1, 0, 0.25F);
		renderBoxes(minecartBoxes, solidBox);
		GL11.glColor4f(0, 1, 0, 0.5F);
		renderBoxes(minecartBoxes, outlinedBox);
		
		if(tracers.isChecked())
		{
			Vec3d start = RotationUtils.getClientLookVec()
				.addVector(0, WMinecraft.getPlayer().getEyeHeight(), 0)
				.addVector(mc.getRenderManager().renderPosX,
					mc.getRenderManager().renderPosY,
					mc.getRenderManager().renderPosZ);
			
			GL11.glBegin(GL11.GL_LINES);
			
			GL11.glColor4f(0, 1, 0, 0.5F);
			renderLines(start, basicChests);
			renderLines(start, minecartBoxes);
			
			GL11.glColor4f(1, 0.5F, 0, 0.5F);
			renderLines(start, trappedChests);
			
			GL11.glColor4f(0, 1, 1, 0.5F);
			renderLines(start, enderChests);
			
			GL11.glEnd();
		}
		
		GL11.glPopMatrix();
		
		// GL resets
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
	}
	
	private void renderBoxes(ArrayList<AxisAlignedBB> boxes, int displayList)
	{
		for(AxisAlignedBB bb : boxes)
		{
			GL11.glPushMatrix();
			GL11.glTranslated(bb.minX, bb.minY, bb.minZ);
			GL11.glScaled(bb.maxX - bb.minX, bb.maxY - bb.minY,
				bb.maxZ - bb.minZ);
			GL11.glCallList(displayList);
			GL11.glPopMatrix();
		}
	}
	
	private void renderLines(Vec3d start, ArrayList<AxisAlignedBB> boxes)
	{
		for(AxisAlignedBB bb : boxes)
		{
			Vec3d end = bb.getCenter();
			GL11.glVertex3d(start.xCoord, start.yCoord, start.zCoord);
			GL11.glVertex3d(end.xCoord, end.yCoord, end.zCoord);
		}
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
			if(!WItem.isNullOrEmpty(chest.inventorySlots.get(i).getStack()))
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
