/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.commands;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.RenderListener;
import net.wurstclient.features.Cmd;
import net.wurstclient.features.HelpPage;
import net.wurstclient.utils.ChatUtils;

@HelpPage("Commands/invsee")
public final class InvseeCmd extends Cmd implements RenderListener
{
	private String playerName;
	
	public InvseeCmd()
	{
		super("invsee",
			"Allows you to see parts of another player's inventory.",
			"<player>");
	}
	
	@Override
	public void call(String[] args) throws CmdException
	{
		if(args.length != 1)
			throw new CmdSyntaxError();
		if(WMinecraft.getPlayer().capabilities.isCreativeMode)
		{
			ChatUtils.error("Survival mode only.");
			return;
		}
		playerName = args[0];
		wurst.events.add(RenderListener.class, this);
	}
	
	@Override
	public void onRender(float partialTicks)
	{
		boolean found = false;
		for(Object entity : WMinecraft.getWorld().loadedEntityList)
			if(entity instanceof EntityOtherPlayerMP)
			{
				EntityOtherPlayerMP player = (EntityOtherPlayerMP)entity;
				if(player.getName().equals(playerName))
				{
					ChatUtils.message(
						"Showing inventory of " + player.getName() + ".");
					mc.displayGuiScreen(new GuiInventory(player));
					found = true;
				}
			}
		if(!found)
			ChatUtils.error("Player not found.");
		playerName = null;
		wurst.events.remove(RenderListener.class, this);
	}
}
