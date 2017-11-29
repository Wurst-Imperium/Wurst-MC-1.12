/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.compatibility;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.EntityRenderer;

public final class WEntityRenderer
{
	public static void drawNameplate(FontRenderer fontRendererIn, String str,
		float x, float y, float z, int verticalShift, float viewerYaw,
		float viewerPitch, boolean isThirdPersonFrontal, boolean isSneaking)
	{
		EntityRenderer.drawNameplate(fontRendererIn, str, x, y, z,
			verticalShift, viewerYaw, viewerPitch, isThirdPersonFrontal,
			isSneaking);
	}
}
