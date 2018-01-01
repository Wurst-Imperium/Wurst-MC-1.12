/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.ai;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.wurstclient.WurstClient;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.utils.RotationUtils;

public abstract class PathProcessor
{
	protected static final WurstClient wurst = WurstClient.INSTANCE;
	protected static final Minecraft mc = Minecraft.getMinecraft();
	
	private static final KeyBinding[] CONTROLS = new KeyBinding[]{
		mc.gameSettings.keyBindForward, mc.gameSettings.keyBindBack,
		mc.gameSettings.keyBindRight, mc.gameSettings.keyBindLeft,
		mc.gameSettings.keyBindJump, mc.gameSettings.keyBindSneak};
	
	protected final ArrayList<PathPos> path;
	protected int index;
	protected boolean done;
	protected int ticksOffPath;
	
	public PathProcessor(ArrayList<PathPos> path)
	{
		if(path.isEmpty())
			throw new IllegalStateException("There is no path!");
		
		this.path = path;
	}
	
	public abstract void process();
	
	public final int getIndex()
	{
		return index;
	}
	
	public final boolean isDone()
	{
		return done;
	}
	
	public final int getTicksOffPath()
	{
		return ticksOffPath;
	}
	
	protected final void facePosition(BlockPos pos)
	{
		RotationUtils
			.faceVectorForWalking(new Vec3d(pos).addVector(0.5, 0.5, 0.5));
	}
	
	public static final void lockControls()
	{
		// disable keys
		for(KeyBinding key : CONTROLS)
			key.pressed = false;
		
		// disable sprinting
		WMinecraft.getPlayer().setSprinting(false);
	}
	
	public static final void releaseControls()
	{
		// reset keys
		for(KeyBinding key : CONTROLS)
			key.pressed = GameSettings.isKeyDown(key);
	}
}
