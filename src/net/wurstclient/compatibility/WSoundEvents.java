/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.compatibility;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketSoundEffect;

public final class WSoundEvents
{
	public static boolean isBobberSplash(SPacketSoundEffect soundEffect)
	{
		return SoundEvents.ENTITY_BOBBER_SPLASH.equals(soundEffect.getSound());
	}
	
	public static void playButtonClick()
	{
		Minecraft.getMinecraft().getSoundHandler()
			.playSound(PositionedSoundRecord
				.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1));
	}
}
