/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.commands;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.TreeMap;

import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;
import net.wurstclient.WurstClient;
import net.wurstclient.events.ChatOutputListener;
import net.wurstclient.features.Cmd;
import net.wurstclient.features.Cmd.CmdException;
import net.wurstclient.utils.ChatUtils;

public final class CmdManager implements ChatOutputListener
{
	private final TreeMap<String, Cmd> cmds =
		new TreeMap<>(new Comparator<String>()
		{
			@Override
			public int compare(String o1, String o2)
			{
				return o1.compareToIgnoreCase(o2);
			}
		});
	
	public final AddAltCmd addAltCmd = new AddAltCmd();
	public final AnnoyCmd annoyCmd = new AnnoyCmd();
	public final AuthorCmd authorCmd = new AuthorCmd();
	public final BindsCmd bindsCmd = new BindsCmd();
	public final BlinkCmd blinkCmd = new BlinkCmd();
	public final ClearCmd clearCmd = new ClearCmd();
	public final CopyItemCmd copyitemCmd = new CopyItemCmd();
	public final DamageCmd damageCmd = new DamageCmd();
	public final DropCmd dropCmd = new DropCmd();
	public final EnchantCmd enchantCmd = new EnchantCmd();
	public final ExcavateCmd excavateCmd = new ExcavateCmd();
	public final FeaturesCmd featuresCmd = new FeaturesCmd();
	public final FollowCmd followCmd = new FollowCmd();
	public final FriendsCmd friendsCmd = new FriendsCmd();
	public final GetPosCmd getPosCmd = new GetPosCmd();
	public final GhostHandCmd ghostHandCmd = new GhostHandCmd();
	public final GiveCmd giveCmd = new GiveCmd();
	public final GmCmd gmCmd = new GmCmd();
	public final GoToCmd goToCmd = new GoToCmd();
	public final HelpCmd HhelpCmd = new HelpCmd();
	public final InvseeCmd invseeCmd = new InvseeCmd();
	public final IpCmd ipCmd = new IpCmd();
	public final JumpCmd jumpCmd = new JumpCmd();
	public final LeaveCmd leaveCmd = new LeaveCmd();
	public final ModifyCmd modifyCmd = new ModifyCmd();
	public final NothingCmd nothingCmd = new NothingCmd();
	public final NukerCmd nukerCmd = new NukerCmd();
	public final PathCmd pathCmd = new PathCmd();
	public final PotionCmd potionCmd = new PotionCmd();
	public final ProtectCmd protectCmd = new ProtectCmd();
	public final RenameCmd renameCmd = new RenameCmd();
	public final RepairCmd repairCmd = new RepairCmd();
	public final RvCmd rvCmd = new RvCmd();
	public final SvCmd svCmd = new SvCmd();
	public final SayCmd sayCmd = new SayCmd();
	public final SearchCmd searchCmd = new SearchCmd();
	public final SetCheckboxCmd setCheckboxCmd = new SetCheckboxCmd();
	public final SetModeCmd setModeCmd = new SetModeCmd();
	public final SetSliderCmd setSliderCmd = new SetSliderCmd();
	public final SpammerCmd spammerCmd = new SpammerCmd();
	public final TacoCmd tacoCmd = new TacoCmd();
	public final TCmd tCmd = new TCmd();
	public final ThrowCmd throwCmd = new ThrowCmd();
	public final TpCmd tpCmd = new TpCmd();
	public final VClipCmd vClipCmd = new VClipCmd();
	public final WmsCmd wmsCmd = new WmsCmd();
	public final XRayCmd xRayCmd = new XRayCmd();
	
	public CmdManager()
	{
		try
		{
			for(Field field : CmdManager.class.getFields())
				if(field.getName().endsWith("Cmd"))
				{
					Cmd cmd = (Cmd)field.get(this);
					cmds.put(cmd.getCmdName(), cmd);
				}
			
		}catch(Exception e)
		{
			throw new ReportedException(
				CrashReport.makeCrashReport(e, "Initializing Wurst commands"));
		}
	}
	
	@Override
	public void onSentMessage(ChatOutputEvent event)
	{
		if(!WurstClient.INSTANCE.isEnabled())
			return;
		
		String message = event.getMessage().trim();
		if(!message.startsWith("."))
			return;
		
		event.cancel();
		
		runCommand(message.substring(1));
	}
	
	public void runCommand(String input)
	{
		String[] parts = input.split(" ");
		Cmd cmd = getCommandByName(parts[0]);
		
		if(cmd == null)
		{
			ChatUtils.error("Unknown command: ." + parts[0]);
			if(input.startsWith("/"))
				ChatUtils.message(
					"Use \".say " + input + "\" to send it as a chat command.");
			else
				ChatUtils
					.message("Type \".help\" for a list of commands or \".say ."
						+ input + "\" to send it as a chat message.");
			return;
		}
		
		try
		{
			cmd.call(Arrays.copyOfRange(parts, 1, parts.length));
			
		}catch(CmdException e)
		{
			e.printToChat();
			
		}catch(Throwable e)
		{
			CrashReport crashReport =
				CrashReport.makeCrashReport(e, "Running Wurst command");
			CrashReportCategory crashReportCategory =
				crashReport.makeCategory("Affected command");
			crashReportCategory.setDetail("Command input", () -> input);
			throw new ReportedException(crashReport);
		}
	}
	
	public Cmd getCommandByName(String name)
	{
		return cmds.get(name);
	}
	
	public Collection<Cmd> getAllCommands()
	{
		return cmds.values();
	}
	
	public int countCommands()
	{
		return cmds.size();
	}
}
