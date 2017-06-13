/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.ai;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.wurstclient.WurstClient;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.utils.RotationUtils;

public abstract class PathProcessor
{
	protected final WurstClient wurst = WurstClient.INSTANCE;
	protected final Minecraft mc = Minecraft.getMinecraft();
	
	protected final ArrayList<PathPos> path;
	protected int index;
	protected boolean done;
	protected boolean failed;
	
	private final KeyBinding[] controls = new KeyBinding[]{
		mc.gameSettings.keyBindForward, mc.gameSettings.keyBindBack,
		mc.gameSettings.keyBindRight, mc.gameSettings.keyBindLeft,
		mc.gameSettings.keyBindJump, mc.gameSettings.keyBindSneak};
	
	public PathProcessor(ArrayList<PathPos> path)
	{
		if(path.isEmpty())
			throw new IllegalStateException("There is no path!");
		
		this.path = path;
	}
	
	public abstract void process();
	
	public void stop()
	{
		releaseControls();
	}
	
	public void lockControls()
	{
		// disable keys
		for(KeyBinding key : controls)
			key.pressed = false;
		
		// face next position
		if(index < path.size())
			facePosition(path.get(index));
		
		// disable sprinting
		WMinecraft.getPlayer().setSprinting(false);
	}
	
	protected void facePosition(BlockPos pos)
	{
		RotationUtils
			.faceVectorForWalking(new Vec3d(pos).addVector(0.5, 0.5, 0.5));
	}
	
	public final void releaseControls()
	{
		// reset keys
		for(KeyBinding key : controls)
			key.pressed = Keyboard.isKeyDown(key.getKeyCode());
	}
	
	public int getIndex()
	{
		return index;
	}
	
	public final boolean isDone()
	{
		return done;
	}
	
	public final boolean isFailed()
	{
		return failed;
	}
}
