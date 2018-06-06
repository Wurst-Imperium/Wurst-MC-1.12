/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.blocks;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.TreeMap;

import org.lwjgl.opengl.GL11;

import com.google.gson.JsonObject;

import net.minecraft.block.material.Material;
import net.minecraft.crash.CrashReport;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ReportedException;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.wurstclient.ai.PathFinder;
import net.wurstclient.ai.PathProcessor;
import net.wurstclient.compatibility.WBlock;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.RenderListener;
import net.wurstclient.events.RightClickListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Feature;
import net.wurstclient.features.HelpPage;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;
import net.wurstclient.features.commands.PathCmd;
import net.wurstclient.features.special_features.YesCheatSpf.Profile;
import net.wurstclient.files.WurstFolders;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.ModeSetting;
import net.wurstclient.utils.BlockUtils;
import net.wurstclient.utils.JsonUtils;
import net.wurstclient.utils.RenderUtils;
import net.wurstclient.utils.RotationUtils;

@SearchTags({"AutoBridge", "AutoFloor", "AutoNazi", "AutoPenis", "AutoPillar",
	"AutoWall", "AutoWurst", "auto build"})
@HelpPage("Mods/AutoBuild")
@Mod.Bypasses
@Mod.DontSaveState
public final class AutoBuildMod extends Mod
	implements RightClickListener, UpdateListener, RenderListener
{
	private final ModeSetting mode = new ModeSetting("Mode",
		"§lFast§r mode can place multiple blocks at once.\n"
			+ "§lLegit§r mode can bypass NoCheat+.",
		new String[]{"Fast", "Legit"}, 0);
	private final CheckboxSetting useAi =
		new CheckboxSetting("Use AI (experimental)", false)
		{
			@Override
			public void update()
			{
				if(!isChecked())
				{
					pathFinder = null;
					processor = null;
					PathProcessor.releaseControls();
				}
			}
		};
	private ModeSetting template;
	
	private int[][][] templates;
	private int blockIndex;
	private final ArrayList<BlockPos> positions = new ArrayList<>();
	
	private AutoBuildPathFinder pathFinder;
	private PathProcessor processor;
	private boolean done;
	
	public AutoBuildMod()
	{
		super("AutoBuild",
			"Automatically builds the selected template whenever you place a block.\n"
				+ "Custom templates can be created by using TemplateTool.");
		setCategory(Category.BLOCKS);
	}
	
	@Override
	public String getRenderName()
	{
		String name = getName() + " [" + template.getSelectedMode() + "]";
		
		if(blockIndex > 0)
			name +=
				" " + (int)((float)blockIndex / (float)positions.size() * 100)
					+ "%";
		
		return name;
	}
	
	@Override
	public void initSettings()
	{
		loadTemplates();
	}
	
	public void setTemplates(TreeMap<String, int[][]> templates)
	{
		// TODO: Find a better way to do this.
		
		getSettings().clear();
		addSetting(mode);
		addSetting(useAi);
		
		this.templates =
			templates.values().toArray(new int[templates.size()][][]);
		
		int selected;
		if(template != null && template.getSelected() < templates.size())
			selected = template.getSelected();
		else
			selected = 0;
		
		template = new ModeSetting("Template",
			templates.keySet().toArray(new String[templates.size()]), selected);
		
		addSetting(template);
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.mods.templateToolMod,
			wurst.mods.buildRandomMod, wurst.mods.fastPlaceMod};
	}
	
	@Override
	public void onEnable()
	{
		wurst.events.add(RightClickListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(RightClickListener.class, this);
		wurst.events.remove(UpdateListener.class, this);
		wurst.events.remove(RenderListener.class, this);
		
		blockIndex = 0;
		
		pathFinder = null;
		processor = null;
		done = false;
		PathProcessor.releaseControls();
	}
	
	@Override
	public void onRightClick(RightClickEvent event)
	{
		// check hitResult
		if(mc.objectMouseOver == null
			|| mc.objectMouseOver.typeOfHit != RayTraceResult.Type.BLOCK
			|| mc.objectMouseOver.getBlockPos() == null || WBlock
				.getMaterial(mc.objectMouseOver.getBlockPos()) == Material.AIR)
			return;
		
		// get start pos and facings
		BlockPos startPos =
			mc.objectMouseOver.getBlockPos().offset(mc.objectMouseOver.sideHit);
		EnumFacing front = WMinecraft.getPlayer().getHorizontalFacing();
		EnumFacing left = front.rotateYCCW();
		
		// set positions
		positions.clear();
		for(int[] pos : templates[template.getSelected()])
			positions.add(
				startPos.up(pos[1]).offset(front, pos[2]).offset(left, pos[0]));
		
		if(mode.getSelected() == 0 && positions.size() <= 64)
		{
			// build instantly
			for(BlockPos pos : positions)
				if(WBlock.getMaterial(pos).isReplaceable())
					BlockUtils.placeBlockSimple_old(pos);
				
		}else
		{
			// initialize building process
			wurst.events.add(UpdateListener.class, this);
			wurst.events.add(RenderListener.class, this);
			wurst.events.remove(RightClickListener.class, this);
		}
	}
	
	@Override
	public void onUpdate()
	{
		// Timer fix
		if(wurst.mods.timerMod.isActive()
			&& wurst.mods.timerMod.getTimerSpeed() > 1)
			blockIndex = 0;
		
		// get next block
		BlockPos pos = positions.get(blockIndex);
		
		// skip already placed blocks
		while(!WBlock.getMaterial(pos).isReplaceable())
		{
			blockIndex++;
			
			// stop if done
			if(blockIndex == positions.size())
			{
				wurst.events.remove(UpdateListener.class, this);
				wurst.events.remove(RenderListener.class, this);
				wurst.events.add(RightClickListener.class, this);
				
				blockIndex = 0;
				
				if(pathFinder != null)
				{
					pathFinder = null;
					processor = null;
					done = false;
					PathProcessor.releaseControls();
				}
				
				return;
			}else
				pos = positions.get(blockIndex);
		}
		
		// move automatically
		if(useAi.isChecked())
		{
			Vec3d eyesPos = RotationUtils.getEyesPos();
			if(WMinecraft.getPlayer().boundingBox
				.intersectsWith(new AxisAlignedBB(pos))
				|| eyesPos.squareDistanceTo(
					new Vec3d(pos).addVector(0.5, 0.5, 0.5)) > 9)
			{
				if(pathFinder != null
					&& (done || !pathFinder.getGoal().equals(pos)))
				{
					pathFinder = null;
					processor = null;
					done = false;
					PathProcessor.releaseControls();
				}
				
				if(pathFinder == null)
				{
					pathFinder = new AutoBuildPathFinder(pos);
					pathFinder.setThinkTime(10);
				}
				
				// find path
				if(!pathFinder.isDone() && !pathFinder.isFailed())
				{
					PathProcessor.lockControls();
					
					pathFinder.think();
					
					if(!pathFinder.isDone() && !pathFinder.isFailed())
						return;
					
					pathFinder.formatPath();
					
					// set processor
					processor = pathFinder.getProcessor();
				}
				
				// check path
				if(processor != null && !pathFinder.isPathStillValid(0))
				{
					pathFinder = new AutoBuildPathFinder(pathFinder);
					return;
				}
				
				// process path
				processor.process();
				
				if(processor.isDone() || processor.getTicksOffPath() >= 40)
					done = true;
				
			}else if(pathFinder != null)
			{
				pathFinder = null;
				processor = null;
				done = false;
				PathProcessor.releaseControls();
			}
		}
		
		// fast mode
		if(mode.getSelected() == 0)
			// place next 64 blocks
			for(int i = blockIndex; i < positions.size()
				&& i < blockIndex + 64; i++)
			{
				pos = positions.get(i);
				if(!WBlock.getMaterial(pos).isReplaceable())
					continue;
				
				if(WMinecraft.getPlayer().boundingBox
					.intersectsWith(new AxisAlignedBB(pos)))
					break;
				
				BlockUtils.placeBlockSimple_old(pos);
			}
		else if(mode.getSelected() == 1)
		{
			// wait for right click timer
			if(mc.rightClickDelayTimer > 0)
				return;
			
			// place next block
			BlockUtils.placeBlockLegit(pos);
		}
	}
	
	@Override
	public void onRender(float partialTicks)
	{
		if(useAi.isChecked() && pathFinder != null)
		{
			PathCmd pathCmd = wurst.commands.pathCmd;
			pathFinder.renderPath(pathCmd.isDebugMode(), pathCmd.isDepthTest());
		}
		
		// scale and offset
		double scale = 1D * 7D / 8D;
		double offset = (1D - scale) / 2D;
		
		// GL settings
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glLineWidth(2F);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_CULL_FACE);
		
		GL11.glPushMatrix();
		GL11.glTranslated(-mc.getRenderManager().renderPosX,
			-mc.getRenderManager().renderPosY,
			-mc.getRenderManager().renderPosZ);
		
		int greenBoxes = mode.getSelected() == 0 ? 64 : 1;
		
		// green boxes
		for(int i = blockIndex; i < positions.size()
			&& i < blockIndex + greenBoxes; i++)
		{
			BlockPos pos = positions.get(i);
			
			if(!WBlock.getMaterial(pos).isReplaceable())
				continue;
			
			GL11.glPushMatrix();
			GL11.glTranslated(pos.getX(), pos.getY(), pos.getZ());
			GL11.glTranslated(offset, offset, offset);
			GL11.glScaled(scale, scale, scale);
			
			GL11.glDepthMask(false);
			GL11.glColor4f(0F, 1F, 0F, 0.15F);
			RenderUtils.drawSolidBox();
			GL11.glDepthMask(true);
			
			GL11.glColor4f(0F, 0F, 0F, 0.5F);
			RenderUtils.drawOutlinedBox();
			
			GL11.glPopMatrix();
		}
		
		// black outlines
		for(int i = blockIndex + greenBoxes; i < positions.size()
			&& i < blockIndex + 1024; i++)
		{
			BlockPos pos = positions.get(i);
			
			GL11.glPushMatrix();
			GL11.glTranslated(pos.getX(), pos.getY(), pos.getZ());
			GL11.glTranslated(offset, offset, offset);
			GL11.glScaled(scale, scale, scale);
			
			RenderUtils.drawOutlinedBox();
			
			GL11.glPopMatrix();
		}
		
		GL11.glPopMatrix();
		
		// GL resets
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
	}
	
	@Override
	public void onYesCheatUpdate(Profile profile)
	{
		if(profile.ordinal() >= Profile.ANTICHEAT.ordinal())
			mode.lock(1);
		else
			mode.unlock();
	}
	
	public void loadTemplates()
	{
		TreeMap<String, int[][]> templates = new TreeMap<>();
		ArrayList<Path> oldTemplates = new ArrayList<>();
		
		try(DirectoryStream<Path> stream =
			Files.newDirectoryStream(WurstFolders.AUTOBUILD, "*.json"))
		{
			for(Path path : stream)
				try(BufferedReader reader = Files.newBufferedReader(path))
				{
					JsonObject json =
						JsonUtils.jsonParser.parse(reader).getAsJsonObject();
					int[][] blocks = JsonUtils.gson.fromJson(json.get("blocks"),
						int[][].class);
					
					if(blocks[0].length == 4)
					{
						oldTemplates.add(path);
						continue;
					}
					
					String name = path.getFileName().toString();
					name = name.substring(0, name.lastIndexOf(".json"));
					templates.put(name, blocks);
					
				}catch(Exception e)
				{
					System.err.println(
						"Failed to load template: " + path.getFileName());
					e.printStackTrace();
				}
			
		}catch(IOException | DirectoryIteratorException e)
		{
			throw new ReportedException(
				CrashReport.makeCrashReport(e, "Loading AutoBuild templates"));
		}
		
		// rename old templates
		for(Path path : oldTemplates)
			try
			{
				Path newPath = path.resolveSibling(path.getFileName() + "_old");
				Files.move(path, newPath);
				
			}catch(IOException e)
			{
				e.printStackTrace();
			}
			
		// if directory is empty or contains old templates,
		// add default templates and try again
		if(templates.isEmpty() || !oldTemplates.isEmpty())
		{
			createDefaultTemplates();
			loadTemplates();
			return;
		}
		
		setTemplates(templates);
	}
	
	private void createDefaultTemplates()
	{
		for(DefaultTemplates template : DefaultTemplates.values())
		{
			JsonObject json = new JsonObject();
			json.add("blocks", JsonUtils.gson.toJsonTree(template.data));
			
			Path path = WurstFolders.AUTOBUILD.resolve(template.name + ".json");
			
			try(BufferedWriter writer = Files.newBufferedWriter(path))
			{
				JsonUtils.prettyGson.toJson(json, writer);
				
			}catch(IOException e)
			{
				System.out.println("Failed to save " + path.getFileName());
				e.printStackTrace();
			}
		}
	}
	
	private static enum DefaultTemplates
	{
		BRIDGE("Bridge",
			new int[][]{{0, 0, 0}, {1, 0, 0}, {1, 0, -1}, {0, 0, -1},
				{-1, 0, -1}, {-1, 0, 0}, {-1, 0, -2}, {0, 0, -2}, {1, 0, -2},
				{1, 0, -3}, {0, 0, -3}, {-1, 0, -3}, {-1, 0, -4}, {0, 0, -4},
				{1, 0, -4}, {1, 0, -5}, {0, 0, -5}, {-1, 0, -5}}),
		
		FLOOR("Floor",
			new int[][]{{0, 0, 0}, {0, 0, 1}, {1, 0, 1}, {1, 0, 0}, {1, 0, -1},
				{0, 0, -1}, {-1, 0, -1}, {-1, 0, 0}, {-1, 0, 1}, {-1, 0, 2},
				{0, 0, 2}, {1, 0, 2}, {2, 0, 2}, {2, 0, 1}, {2, 0, 0},
				{2, 0, -1}, {2, 0, -2}, {1, 0, -2}, {0, 0, -2}, {-1, 0, -2},
				{-2, 0, -2}, {-2, 0, -1}, {-2, 0, 0}, {-2, 0, 1}, {-2, 0, 2},
				{-2, 0, 3}, {-1, 0, 3}, {0, 0, 3}, {1, 0, 3}, {2, 0, 3},
				{3, 0, 3}, {3, 0, 2}, {3, 0, 1}, {3, 0, 0}, {3, 0, -1},
				{3, 0, -2}, {3, 0, -3}, {2, 0, -3}, {1, 0, -3}, {0, 0, -3},
				{-1, 0, -3}, {-2, 0, -3}, {-3, 0, -3}, {-3, 0, -2}, {-3, 0, -1},
				{-3, 0, 0}, {-3, 0, 1}, {-3, 0, 2}, {-3, 0, 3}}),
		
		PENIS("Penis", new int[][]{{0, 0, 0}, {0, 0, 1}, {1, 0, 1}, {1, 0, 0},
			{1, 1, 0}, {0, 1, 0}, {0, 1, 1}, {1, 1, 1}, {1, 2, 1}, {0, 2, 1},
			{0, 2, 0}, {1, 2, 0}, {1, 3, 0}, {0, 3, 0}, {0, 3, 1}, {1, 3, 1},
			{1, 4, 1}, {0, 4, 1}, {0, 4, 0}, {1, 4, 0}, {1, 5, 0}, {0, 5, 0},
			{0, 5, 1}, {1, 5, 1}, {1, 6, 1}, {0, 6, 1}, {0, 6, 0}, {1, 6, 0},
			{1, 7, 0}, {0, 7, 0}, {0, 7, 1}, {1, 7, 1}, {-1, 0, -1},
			{-1, 1, -1}, {-2, 1, -1}, {-2, 0, -1}, {-2, 0, -2}, {-1, 0, -2},
			{-1, 1, -2}, {-2, 1, -2}, {2, 0, -1}, {2, 1, -1}, {2, 1, -2},
			{2, 0, -2}, {3, 0, -2}, {3, 0, -1}, {3, 1, -1}, {3, 1, -2}}),
		
		PILLAR("Pillar",
			new int[][]{{0, 0, 0}, {0, 1, 0}, {0, 2, 0}, {0, 3, 0}, {0, 4, 0},
				{0, 5, 0}, {0, 6, 0}}),
		
		SWASTIKA("Swastika",
			new int[][]{{0, 0, 0}, {1, 0, 0}, {2, 0, 0}, {0, 1, 0}, {0, 2, 0},
				{1, 2, 0}, {2, 2, 0}, {2, 3, 0}, {2, 4, 0}, {0, 3, 0},
				{0, 4, 0}, {-1, 4, 0}, {-2, 4, 0}, {-1, 2, 0}, {-2, 2, 0},
				{-2, 1, 0}, {-2, 0, 0}}),
		
		WALL("Wall",
			new int[][]{{0, 0, 0}, {1, 0, 0}, {1, 1, 0}, {0, 1, 0}, {-1, 1, 0},
				{-1, 0, 0}, {-2, 0, 0}, {-2, 1, 0}, {-2, 2, 0}, {-1, 2, 0},
				{0, 2, 0}, {1, 2, 0}, {2, 2, 0}, {2, 1, 0}, {2, 0, 0},
				{3, 0, 0}, {3, 1, 0}, {3, 2, 0}, {3, 3, 0}, {2, 3, 0},
				{1, 3, 0}, {0, 3, 0}, {-1, 3, 0}, {-2, 3, 0}, {-3, 3, 0},
				{-3, 2, 0}, {-3, 1, 0}, {-3, 0, 0}, {-3, 4, 0}, {-2, 4, 0},
				{-1, 4, 0}, {0, 4, 0}, {1, 4, 0}, {2, 4, 0}, {3, 4, 0},
				{3, 5, 0}, {2, 5, 0}, {1, 5, 0}, {0, 5, 0}, {-1, 5, 0},
				{-2, 5, 0}, {-3, 5, 0}, {-3, 6, 0}, {-2, 6, 0}, {-1, 6, 0},
				{0, 6, 0}, {1, 6, 0}, {2, 6, 0}, {3, 6, 0}}),
		
		WURST("Wurst",
			new int[][]{{0, 0, 0}, {1, 0, 0}, {1, 1, 0}, {0, 1, 0}, {0, 1, 1},
				{1, 1, 1}, {2, 1, 1}, {2, 1, 0}, {2, 0, 0}, {2, 1, -1},
				{1, 1, -1}, {0, 1, -1}, {-1, 1, -1}, {-1, 1, 0}, {-1, 0, 0},
				{-2, 0, 0}, {-2, 1, 0}, {-2, 1, 1}, {-1, 1, 1}, {-1, 2, 0},
				{0, 2, 0}, {1, 2, 0}, {2, 2, 0}, {3, 1, 0}, {-2, 1, -1},
				{-2, 2, 0}, {-3, 1, 0}});
		
		private final String name;
		private final int[][] data;
		
		private DefaultTemplates(String name, int[][] data)
		{
			this.name = name;
			this.data = data;
		}
	}
	
	private class AutoBuildPathFinder extends PathFinder
	{
		public AutoBuildPathFinder(BlockPos goal)
		{
			super(goal);
		}
		
		public AutoBuildPathFinder(AutoBuildPathFinder pathFinder)
		{
			super(pathFinder);
		}
		
		@Override
		protected boolean checkDone()
		{
			BlockPos goal = getGoal();
			
			return done = goal.down(2).equals(current)
				|| goal.up().equals(current) || goal.north().equals(current)
				|| goal.south().equals(current) || goal.west().equals(current)
				|| goal.east().equals(current)
				|| goal.down().north().equals(current)
				|| goal.down().south().equals(current)
				|| goal.down().west().equals(current)
				|| goal.down().east().equals(current);
		}
	}
}
