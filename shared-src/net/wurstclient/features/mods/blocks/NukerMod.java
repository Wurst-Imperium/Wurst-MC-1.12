/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.blocks;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.wurstclient.compatibility.WBlock;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.LeftClickListener;
import net.wurstclient.events.PostUpdateListener;
import net.wurstclient.events.RenderListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Feature;
import net.wurstclient.features.Mod;
import net.wurstclient.features.special_features.YesCheatSpf.Profile;
import net.wurstclient.settings.ModeSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;
import net.wurstclient.utils.BlockUtils;
import net.wurstclient.utils.RenderUtils;
import net.wurstclient.utils.RotationUtils;

@Mod.Bypasses
public final class NukerMod extends Mod implements LeftClickListener,
	UpdateListener, PostUpdateListener, RenderListener
{
	public final SliderSetting range =
		new SliderSetting("Range", 6, 1, 6, 0.05, ValueDisplay.DECIMAL);
	public final ModeSetting mode = new ModeSetting("Mode",
		new String[]{"Normal", "ID", "Flat", "Smash"}, 0);
	private final ModeSetting mode2 =
		new ModeSetting("Mode 2", new String[]{"Fast", "Legit"}, 0);
	
	private final ArrayDeque<Set<BlockPos>> prevBlocks = new ArrayDeque<>();
	private BlockPos currentBlock;
	private float progress;
	private float prevProgress;
	private int id;
	
	public NukerMod()
	{
		super("Nuker", "Automatically breaks blocks around you.");
		setCategory(Category.BLOCKS);
		addSetting(range);
		addSetting(mode);
		addSetting(mode2);
	}
	
	@Override
	public String getRenderName()
	{
		return Mode.values()[mode.getSelected()].getRenderName(this);
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.mods.nukerLegitMod, wurst.mods.speedNukerMod,
			wurst.mods.tunnellerMod};
	}
	
	@Override
	public void onEnable()
	{
		// disable other nukers
		wurst.mods.nukerLegitMod.setEnabled(false);
		wurst.mods.speedNukerMod.setEnabled(false);
		wurst.mods.tunnellerMod.setEnabled(false);
		
		// add listeners
		wurst.events.add(LeftClickListener.class, this);
		wurst.events.add(UpdateListener.class, this);
		wurst.events.add(PostUpdateListener.class, this);
		wurst.events.add(RenderListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		// remove listeners
		wurst.events.remove(LeftClickListener.class, this);
		wurst.events.remove(UpdateListener.class, this);
		wurst.events.remove(PostUpdateListener.class, this);
		wurst.events.remove(RenderListener.class, this);
		
		if(currentBlock != null)
		{
			mc.playerController.isHittingBlock = true;
			mc.playerController.resetBlockRemoving();
		}
		
		prevBlocks.clear();
		currentBlock = null;
		id = 0;
	}
	
	@Override
	public void onUpdate()
	{
		// abort if using IDNuker without an ID being set
		if(mode.getSelected() == 1 && id == 0)
			return;
		
		currentBlock = null;
		Vec3d eyesPos = RotationUtils.getEyesPos().subtract(0.5, 0.5, 0.5);
		BlockPos eyesBlock = new BlockPos(RotationUtils.getEyesPos());
		double rangeSq = Math.pow(range.getValue(), 2);
		int blockRange = (int)Math.ceil(range.getValue());
		boolean legit = mode2.getSelected() == 1;
		
		Stream<BlockPos> stream = StreamSupport.stream(BlockPos
			.getAllInBox(eyesBlock.add(blockRange, blockRange, blockRange),
				eyesBlock.add(-blockRange, -blockRange, -blockRange))
			.spliterator(), true);
		
		List<BlockPos> blocks = stream
			.filter(pos -> eyesPos.squareDistanceTo(new Vec3d(pos)) <= rangeSq)
			.filter(pos -> WBlock.canBeClicked(pos))
			.filter(Mode.values()[mode.getSelected()].getValidator(this))
			.sorted(Comparator.comparingDouble(
				pos -> eyesPos.squareDistanceTo(new Vec3d(pos))))
			.collect(Collectors.toList());
		
		if(WMinecraft.getPlayer().capabilities.isCreativeMode && !legit)
		{
			Stream<BlockPos> stream2 = blocks.parallelStream();
			for(Set<BlockPos> set : prevBlocks)
				stream2 = stream2.filter(pos -> !set.contains(pos));
			List<BlockPos> blocks2 = stream2.collect(Collectors.toList());
			
			prevBlocks.addLast(new HashSet<>(blocks2));
			while(prevBlocks.size() > 5)
				prevBlocks.removeFirst();
			
			if(!blocks2.isEmpty())
				currentBlock = blocks2.get(0);
			
			mc.playerController.resetBlockRemoving();
			progress = 1;
			prevProgress = 1;
			BlockUtils.breakBlocksPacketSpam(blocks2);
			return;
		}
		
		// find closest valid block
		for(BlockPos pos : blocks)
		{
			// break block
			boolean successful =
				legit ? BlockUtils.prepareToBreakBlockLegit(pos)
					: BlockUtils.breakBlockSimple_old(pos);
			
			// set currentBlock if successful
			if(successful)
			{
				currentBlock = pos;
				break;
			}
		}
		
		// reset if no block was found
		if(currentBlock == null)
			mc.playerController.resetBlockRemoving();
		
		if(currentBlock != null && WBlock.getHardness(currentBlock) < 1
			&& !WMinecraft.getPlayer().capabilities.isCreativeMode)
		{
			prevProgress = progress;
			progress = mc.playerController.curBlockDamageMP;
			if(progress < prevProgress)
				prevProgress = progress;
			
		}else
		{
			progress = 1;
			prevProgress = 1;
		}
	}
	
	@Override
	public void afterUpdate()
	{
		boolean legit = mode2.getSelected() == 1;
		
		// break block
		if(currentBlock != null && legit)
			BlockUtils.breakBlockLegit(currentBlock);
	}
	
	@Override
	public void onLeftClick(LeftClickEvent event)
	{
		// check hitResult
		if(mc.objectMouseOver == null
			|| mc.objectMouseOver.getBlockPos() == null
			|| mc.objectMouseOver.typeOfHit != Type.BLOCK)
			return;
		
		// set id
		if(mode.getSelected() == Mode.ID.ordinal())
			id = WBlock.getId(mc.objectMouseOver.getBlockPos());
	}
	
	@Override
	public void onRender(float partialTicks)
	{
		if(currentBlock == null)
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
		GL11.glTranslated(-TileEntityRendererDispatcher.staticPlayerX,
			-TileEntityRendererDispatcher.staticPlayerY,
			-TileEntityRendererDispatcher.staticPlayerZ);
		
		AxisAlignedBB box = new AxisAlignedBB(BlockPos.ORIGIN);
		float p = prevProgress + (progress - prevProgress) * partialTicks;
		float red = p * 2F;
		float green = 2 - red;
		
		GL11.glTranslated(currentBlock.getX(), currentBlock.getY(),
			currentBlock.getZ());
		if(p < 1)
		{
			GL11.glTranslated(0.5, 0.5, 0.5);
			GL11.glScaled(p, p, p);
			GL11.glTranslated(-0.5, -0.5, -0.5);
		}
		
		GL11.glColor4f(red, green, 0, 0.25F);
		RenderUtils.drawSolidBox(box);
		GL11.glColor4f(red, green, 0, 0.5F);
		RenderUtils.drawOutlinedBox(box);
		
		GL11.glPopMatrix();
		
		// GL resets
		GL11.glColor4f(1, 1, 1, 1);
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
			default:
			case OFF:
			case MINEPLEX:
			range.resetUsableMax();
			mode2.unlock();
			break;
			
			case ANTICHEAT:
			case OLDER_NCP:
			case LATEST_NCP:
			case GHOST_MODE:
			range.setUsableMax(4.25);
			mode2.lock(1);
			break;
		}
	}
	
	public int getId()
	{
		return id;
	}
	
	public void setId(int id)
	{
		this.id = id;
	}
	
	private enum Mode
	{
		NORMAL("Normal", n -> n.getName(), (n, p) -> true),
		
		ID("ID", n -> "IDNuker [" + n.id + "]",
			(n, p) -> WBlock.getId(p) == n.id),
		
		FLAT("Flat", n -> "FlatNuker",
			(n, p) -> p.getY() >= WMinecraft.getPlayer().getPosition().getY()),
		
		SMASH("Smash", n -> "SmashNuker", (n, p) -> WBlock.getHardness(p) >= 1);
		
		private final String name;
		private final Function<NukerMod, String> renderName;
		private final BiPredicate<NukerMod, BlockPos> validator;
		
		private Mode(String name, Function<NukerMod, String> renderName,
			BiPredicate<NukerMod, BlockPos> validator)
		{
			this.name = name;
			this.renderName = renderName;
			this.validator = validator;
		}
		
		@Override
		public String toString()
		{
			return name;
		}
		
		public String getRenderName(NukerMod n)
		{
			return renderName.apply(n);
		}
		
		public Predicate<BlockPos> getValidator(NukerMod n)
		{
			return p -> validator.test(n, p);
		}
	}
}
