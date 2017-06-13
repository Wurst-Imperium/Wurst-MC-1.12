/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.gui;

import org.lwjgl.opengl.GL11;

import net.wurstclient.WurstClient;
import net.wurstclient.events.GUIRenderEvent;

public final class UIRenderer
{
	private static final WurstLogo wurstLogo = new WurstLogo();
	public static final ModList modList = new ModList();
	private static final TabGui tabGui = new TabGui();
	
	public static void renderUI(float partialTicks)
	{
		if(!WurstClient.INSTANCE.isEnabled())
			return;
		
		// GL settings
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		wurstLogo.render();
		modList.render(partialTicks);
		tabGui.render(partialTicks);
		
		WurstClient.INSTANCE.events.fire(GUIRenderEvent.INSTANCE);
		
		// GL resets
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glColor4f(1, 1, 1, 1);
	}
}
