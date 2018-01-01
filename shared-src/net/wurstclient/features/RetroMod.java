/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features;

import net.wurstclient.compatibility.WMinecraft;

public abstract class RetroMod extends Mod
{
	private static final String NOTICE =
		"\n\n§6§lNotice:§r This mod only works on servers that are based on Minecraft 1.8 (such as Mineplex).";
	
	public RetroMod(String name, String description)
	{
		super(name, WMinecraft.COOLDOWN ? description + NOTICE : description);
	}
}
