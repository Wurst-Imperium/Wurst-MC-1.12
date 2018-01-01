/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.commands;

import net.minecraft.entity.Entity;
import net.wurstclient.features.Cmd;
import net.wurstclient.features.HelpPage;
import net.wurstclient.utils.EntityUtils;
import net.wurstclient.utils.EntityUtils.TargetSettings;

@HelpPage("Commands/protect")
public final class ProtectCmd extends Cmd
{
	private TargetSettings targetSettings = new TargetSettings()
	{
		@Override
		public boolean targetFriends()
		{
			return true;
		}
		
		@Override
		public boolean targetBehindWalls()
		{
			return true;
		}
	};
	
	public ProtectCmd()
	{
		super("protect",
			"Toggles Protect or makes it protect a specific entity.",
			"[<entity>]");
	}
	
	@Override
	public void call(String[] args) throws CmdException
	{
		if(args.length > 1)
			throw new CmdSyntaxError();
		if(args.length == 0)
			wurst.mods.protectMod.toggle();
		else
		{
			if(wurst.mods.protectMod.isEnabled())
				wurst.mods.protectMod.setEnabled(false);
			Entity entity =
				EntityUtils.getClosestEntityWithName(args[0], targetSettings);
			if(entity == null)
				throw new CmdError(
					"Entity \"" + args[0] + "\" could not be found.");
			wurst.mods.protectMod.setEnabled(true);
			wurst.mods.protectMod.setFriend(entity);
		}
	}
}
