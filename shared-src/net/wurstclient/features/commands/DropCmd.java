/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.commands;

import java.util.Random;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketCreativeInventoryAction;
import net.wurstclient.compatibility.WConnection;
import net.wurstclient.compatibility.WPlayerController;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Cmd;
import net.wurstclient.features.HelpPage;
import net.wurstclient.features.special_features.YesCheatSpf.Profile;

@HelpPage("Commands/drop")
public final class DropCmd extends Cmd implements UpdateListener
{
	private int timer;
	private int counter;
	private boolean infinite;
	
	public DropCmd()
	{
		super("drop", "Drops all your items on the ground.", "[infinite]");
	}
	
	@Override
	public void call(String[] args) throws CmdException
	{
		if(args.length > 1)
			throw new CmdSyntaxError();
		if(args.length == 1)
			if(args[0].equalsIgnoreCase("infinite"))
				infinite = !infinite;
			else
				throw new CmdSyntaxError();
		else
			infinite = false;
		timer = 0;
		counter = 9;
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		if(infinite)
		{
			Item item = null;
			while(item == null)
				item = Item.getItemById(new Random().nextInt(431));
			WConnection.sendPacket(new CPacketCreativeInventoryAction(-1,
				new ItemStack(item, 64)));
			return;
		}
		if(wurst.special.yesCheatSpf.getProfile().ordinal() >= Profile.OLDER_NCP
			.ordinal())
		{
			timer++;
			if(timer >= 5)
			{
				WPlayerController.windowClick_THROW(counter);
				counter++;
				timer = 0;
				if(counter >= 45)
					wurst.events.remove(UpdateListener.class, this);
			}
		}else
		{
			for(int i = 9; i < 45; i++)
				WPlayerController.windowClick_THROW(i);
			wurst.events.remove(UpdateListener.class, this);
		}
	}
}
