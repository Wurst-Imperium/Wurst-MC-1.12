/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.commands;

import net.minecraft.util.math.BlockPos;
import net.wurstclient.features.Cmd;
import net.wurstclient.features.Feature;

public final class ExcavateCmd extends Cmd
{
	public ExcavateCmd()
	{
		super("excavate",
			"Automatically destroys all blocks in the selected area.",
			"<x1> <y1> <z1> <x2> <y2> <z2>");
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.mods.excavatorMod};
	}
	
	@Override
	public void call(String[] args) throws CmdException
	{
		if(args.length != 6)
			throw new CmdSyntaxError();
		
		BlockPos pos1 = argsToPos(args[0], args[1], args[2]);
		BlockPos pos2 = argsToPos(args[3], args[4], args[5]);
		wurst.mods.excavatorMod.enableWithArea(pos1, pos2);
	}
}
