/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.CameraTransformViewBobbingListener;
import net.wurstclient.events.PlayerMoveListener;
import net.wurstclient.events.RenderListener;
import net.wurstclient.events.SetOpaqueCubeListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Category;
import net.wurstclient.features.Feature;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;
import net.wurstclient.utils.EntityFakePlayer;
import net.wurstclient.utils.RenderUtils;
import net.wurstclient.utils.RotationUtils;

@SearchTags({"free camera", "spectator"})
@Mod.Bypasses
@Mod.DontSaveState
public final class FreecamMod extends Mod
	implements UpdateListener, PlayerMoveListener,
	CameraTransformViewBobbingListener, SetOpaqueCubeListener, RenderListener
{
	private final SliderSetting speed =
		new SliderSetting("Speed", 1, 0.05, 10, 0.05, ValueDisplay.DECIMAL);
	private final CheckboxSetting tracer = new CheckboxSetting("Tracer",
		"Draws a line to your character's actual position.", false);
	
	private EntityFakePlayer fakePlayer;
	private int playerBox;
	
	public FreecamMod()
	{
		super("Freecam",
			"Allows you to move the camera without moving your character.");
		setCategory(Category.RENDER);
		addSetting(speed);
		addSetting(tracer);
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.mods.remoteViewMod};
	}
	
	@Override
	public void onEnable()
	{
		wurst.events.add(UpdateListener.class, this);
		wurst.events.add(PlayerMoveListener.class, this);
		wurst.events.add(CameraTransformViewBobbingListener.class, this);
		wurst.events.add(SetOpaqueCubeListener.class, this);
		wurst.events.add(RenderListener.class, this);
		
		fakePlayer = new EntityFakePlayer();
		
		GameSettings gs = mc.gameSettings;
		KeyBinding[] bindings = {gs.keyBindForward, gs.keyBindBack,
			gs.keyBindLeft, gs.keyBindRight, gs.keyBindJump, gs.keyBindSneak};
		
		for(KeyBinding binding : bindings)
			binding.pressed = GameSettings.isKeyDown(binding);
		
		playerBox = GL11.glGenLists(1);
		GL11.glNewList(playerBox, GL11.GL_COMPILE);
		AxisAlignedBB bb = new AxisAlignedBB(-0.5, 0, -0.5, 0.5, 1, 0.5);
		RenderUtils.drawOutlinedBox(bb);
		GL11.glEndList();
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
		wurst.events.remove(PlayerMoveListener.class, this);
		wurst.events.remove(CameraTransformViewBobbingListener.class, this);
		wurst.events.remove(SetOpaqueCubeListener.class, this);
		wurst.events.remove(RenderListener.class, this);
		
		fakePlayer.resetPlayerPosition();
		fakePlayer.despawn();
		
		EntityPlayerSP player = WMinecraft.getPlayer();
		player.motionX = 0;
		player.motionY = 0;
		player.motionZ = 0;
		
		mc.renderGlobal.loadRenderers();
		
		GL11.glDeleteLists(playerBox, 1);
		playerBox = 0;
	}
	
	@Override
	public void onUpdate()
	{
		EntityPlayerSP player = WMinecraft.getPlayer();
		player.motionX = 0;
		player.motionY = 0;
		player.motionZ = 0;
		
		player.onGround = false;
		player.jumpMovementFactor = speed.getValueF();
		
		if(mc.gameSettings.keyBindJump.pressed)
			player.motionY += speed.getValue();
		
		if(mc.gameSettings.keyBindSneak.pressed)
			player.motionY -= speed.getValue();
	}
	
	@Override
	public void onPlayerMove(EntityPlayerSP player)
	{
		player.noClip = true;
	}
	
	@Override
	public void onCameraTransformViewBobbing(
		CameraTransformViewBobbingEvent event)
	{
		if(tracer.isChecked())
			event.cancel();
	}
	
	@Override
	public void onSetOpaqueCube(SetOpaqueCubeEvent event)
	{
		event.cancel();
	}
	
	@Override
	public void onRender(float partialTicks)
	{
		if(fakePlayer == null || !tracer.isChecked())
			return;
		
		// GL settings
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glLineWidth(2);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		GL11.glPushMatrix();
		GL11.glTranslated(-mc.getRenderManager().renderPosX,
			-mc.getRenderManager().renderPosY,
			-mc.getRenderManager().renderPosZ);
		
		GL11.glColor4f(1, 1, 1, 0.5F);
		
		// box
		GL11.glPushMatrix();
		GL11.glTranslated(fakePlayer.posX, fakePlayer.posY, fakePlayer.posZ);
		GL11.glScaled(fakePlayer.width + 0.1, fakePlayer.height + 0.1,
			fakePlayer.width + 0.1);
		GL11.glCallList(playerBox);
		GL11.glPopMatrix();
		
		// line
		Vec3d start = RotationUtils.getClientLookVec()
			.addVector(0, WMinecraft.getPlayer().getEyeHeight(), 0)
			.addVector(mc.getRenderManager().renderPosX,
				mc.getRenderManager().renderPosY,
				mc.getRenderManager().renderPosZ);
		Vec3d end = fakePlayer.getEntityBoundingBox().getCenter();
		
		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex3d(start.xCoord, start.yCoord, start.zCoord);
		GL11.glVertex3d(end.xCoord, end.yCoord, end.zCoord);
		GL11.glEnd();
		
		GL11.glPopMatrix();
		
		// GL resets
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
	}
}
