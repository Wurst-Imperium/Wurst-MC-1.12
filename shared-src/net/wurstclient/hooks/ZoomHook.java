/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.hooks;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import net.wurstclient.WurstClient;
import net.wurstclient.options.OptionsManager.Zoom;

public class ZoomHook
{
	private static float scrollLevel = WurstClient.INSTANCE.options.zoom.level;
	
	public static float changeFovBasedOnZoom(float fov)
	{
		Zoom zoom = WurstClient.INSTANCE.options.zoom;
		if(Keyboard.isKeyDown(zoom.keybind))
		{
			if(zoom.scroll)
			{
				int scroll = Mouse.getDWheel();
				if(scroll > 0)
					scrollLevel =
						Math.min(Math.round(scrollLevel * 11F) / 10F, 10F);
				else if(scroll < 0)
					scrollLevel =
						Math.max(Math.round(scrollLevel * 9F) / 10F, 1F);
				fov /= scrollLevel;
			}else
				fov /= zoom.level;
		}else if(scrollLevel != zoom.level)
			scrollLevel = zoom.level;
		return fov;
	}
}
