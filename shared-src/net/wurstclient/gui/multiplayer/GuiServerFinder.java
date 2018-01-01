/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.gui.multiplayer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.multiplayer.ServerData;
import net.wurstclient.WurstClient;
import net.wurstclient.files.ConfigFiles;
import net.wurstclient.servers.WurstServerPinger;
import net.wurstclient.utils.MiscUtils;

public class GuiServerFinder extends GuiScreen
{
	private static final String[] stateStrings =
		{"", "§2Searching...", "§2Resolving...", "§4Unknown Host!",
			"§4Cancelled!", "§2Done!", "§4An error occurred!"};
	
	enum ServerFinderState
	{
		NOT_RUNNING,
		SEARCHING,
		RESOLVING,
		UNKNOWN_HOST,
		CANCELLED,
		DONE,
		ERROR;
		
		public boolean isRunning()
		{
			return this == SEARCHING || this == RESOLVING;
		}
		
		@Override
		public String toString()
		{
			return stateStrings[ordinal()];
		}
	}
	
	private GuiMultiplayer prevScreen;
	private GuiTextField ipBox;
	private GuiTextField maxThreadsBox;
	private int checked;
	private int working;
	private ServerFinderState state;
	
	public GuiServerFinder(GuiMultiplayer prevMultiplayerMenu)
	{
		prevScreen = prevMultiplayerMenu;
	}
	
	/**
	 * Called from the main game loop to update the screen.
	 */
	@Override
	public void updateScreen()
	{
		ipBox.updateCursorCounter();
		
		buttonList.get(0).displayString =
			state.isRunning() ? "Cancel" : "Search";
		ipBox.setEnabled(!state.isRunning());
		maxThreadsBox.setEnabled(!state.isRunning());
		
		buttonList.get(0).enabled = MiscUtils.isInteger(maxThreadsBox.getText())
			&& !ipBox.getText().isEmpty();
	}
	
	/**
	 * Adds the buttons (and other controls) to the screen in question.
	 */
	@Override
	public void initGui()
	{
		Keyboard.enableRepeatEvents(true);
		buttonList.clear();
		buttonList.add(
			new GuiButton(0, width / 2 - 100, height / 4 + 96 + 12, "Search"));
		buttonList.add(new GuiButton(1, width / 2 - 100, height / 4 + 120 + 12,
			"Tutorial"));
		buttonList.add(
			new GuiButton(2, width / 2 - 100, height / 4 + 144 + 12, "Back"));
		ipBox = new GuiTextField(0, fontRendererObj, width / 2 - 100,
			height / 4 + 34, 200, 20);
		ipBox.setMaxStringLength(200);
		ipBox.setFocused(true);
		maxThreadsBox = new GuiTextField(1, fontRendererObj, width / 2 - 32,
			height / 4 + 58, 26, 12);
		maxThreadsBox.setMaxStringLength(3);
		maxThreadsBox.setFocused(false);
		maxThreadsBox.setText(
			Integer.toString(WurstClient.INSTANCE.options.serverFinderThreads));
		
		state = ServerFinderState.NOT_RUNNING;
		
		WurstClient.INSTANCE.analytics
			.trackPageView("/multiplayer/server-finder", "Server Finder");
	}
	
	/**
	 * "Called when the screen is unloaded. Used to disable keyboard repeat
	 * events."
	 */
	@Override
	public void onGuiClosed()
	{
		state = ServerFinderState.CANCELLED;
		WurstClient.INSTANCE.analytics.trackEvent("server finder", "cancel",
			"gui closed", working);
		
		if(MiscUtils.isInteger(maxThreadsBox.getText()))
		{
			WurstClient.INSTANCE.options.serverFinderThreads =
				Integer.valueOf(maxThreadsBox.getText());
			ConfigFiles.OPTIONS.save();
		}
		Keyboard.enableRepeatEvents(false);
	}
	
	@Override
	protected void actionPerformed(GuiButton clickedButton)
	{
		if(clickedButton.enabled)
			if(clickedButton.id == 0)
			{// Search / Cancel
				if(state.isRunning())
				{
					state = ServerFinderState.CANCELLED;
					WurstClient.INSTANCE.analytics.trackEvent("server finder",
						"cancel", "cancel button", working);
				}else
				{
					if(MiscUtils.isInteger(maxThreadsBox.getText()))
					{
						WurstClient.INSTANCE.options.serverFinderThreads =
							Integer.valueOf(maxThreadsBox.getText());
						ConfigFiles.OPTIONS.save();
					}
					
					state = ServerFinderState.RESOLVING;
					checked = 0;
					working = 0;
					
					new Thread("Server Finder")
					{
						@Override
						public void run()
						{
							try
							{
								InetAddress addr = InetAddress.getByName(
									ipBox.getText().split(":")[0].trim());
								
								int[] ipParts = new int[4];
								for(int i = 0; i < 4; i++)
									ipParts[i] = addr.getAddress()[i] & 0xff;
								
								state = ServerFinderState.SEARCHING;
								ArrayList<WurstServerPinger> pingers =
									new ArrayList<>();
								int[] changes = {0, 1, -1, 2, -2, 3, -3};
								for(int change : changes)
									for(int i2 = 0; i2 <= 255; i2++)
									{
										if(state == ServerFinderState.CANCELLED)
											return;
										
										int[] ipParts2 = ipParts.clone();
										ipParts2[2] =
											ipParts[2] + change & 0xff;
										ipParts2[3] = i2;
										String ip = ipParts2[0] + "."
											+ ipParts2[1] + "." + ipParts2[2]
											+ "." + ipParts2[3];
										
										WurstServerPinger pinger =
											new WurstServerPinger();
										pinger.ping(ip);
										pingers.add(pinger);
										while(pingers
											.size() >= WurstClient.INSTANCE.options.serverFinderThreads)
										{
											if(state == ServerFinderState.CANCELLED)
												return;
											
											updatePingers(pingers);
										}
									}
								while(pingers.size() > 0)
								{
									if(state == ServerFinderState.CANCELLED)
										return;
									
									updatePingers(pingers);
								}
								WurstClient.INSTANCE.analytics.trackEvent(
									"server finder", "complete", "", working);
								state = ServerFinderState.DONE;
							}catch(UnknownHostException e)
							{
								state = ServerFinderState.UNKNOWN_HOST;
								WurstClient.INSTANCE.analytics.trackEvent(
									"server finder", "unknown host");
							}catch(Exception e)
							{
								e.printStackTrace();
								state = ServerFinderState.ERROR;
								WurstClient.INSTANCE.analytics
									.trackEvent("server finder", "error");
							}
						}
					}.start();
					WurstClient.INSTANCE.analytics.trackEvent("server finder",
						"start");
				}
			}else if(clickedButton.id == 1)
				MiscUtils.openLink(
					"https://www.wurstclient.net/wiki/Special_Features/Server_Finder/");
			else if(clickedButton.id == 2)
				mc.displayGuiScreen(prevScreen);
	}
	
	private boolean serverInList(String ip)
	{
		for(int i = 0; i < prevScreen.savedServerList.countServers(); i++)
			if(prevScreen.savedServerList.getServerData(i).serverIP.equals(ip))
				return true;
			
		return false;
	}
	
	private void updatePingers(ArrayList<WurstServerPinger> pingers)
	{
		for(int i = 0; i < pingers.size(); i++)
			if(!pingers.get(i).isStillPinging())
			{
				GuiServerFinder.this.checked++;
				if(pingers.get(i).isWorking())
				{
					GuiServerFinder.this.working++;
					
					if(!serverInList(pingers.get(i).server.serverIP))
					{
						GuiServerFinder.this.prevScreen.savedServerList
							.addServerData(
								new ServerData("Grief me #" + working,
									pingers.get(i).server.serverIP, false));
						GuiServerFinder.this.prevScreen.savedServerList
							.saveServerList();
						GuiServerFinder.this.prevScreen.serverListSelector
							.setSelectedSlotIndex(-1);
						GuiServerFinder.this.prevScreen.serverListSelector
							.updateOnlineServers(
								GuiServerFinder.this.prevScreen.savedServerList);
					}
				}
				pingers.remove(i);
			}
	}
	
	/**
	 * Fired when a key is typed. This is the equivalent of
	 * KeyListener.keyTyped(KeyEvent e).
	 */
	@Override
	protected void keyTyped(char par1, int par2)
	{
		ipBox.textboxKeyTyped(par1, par2);
		maxThreadsBox.textboxKeyTyped(par1, par2);
		
		if(par2 == 28 || par2 == 156)
			actionPerformed(buttonList.get(0));
	}
	
	/**
	 * Called when the mouse is clicked.
	 *
	 * @throws IOException
	 */
	@Override
	protected void mouseClicked(int par1, int par2, int par3) throws IOException
	{
		super.mouseClicked(par1, par2, par3);
		ipBox.mouseClicked(par1, par2, par3);
		maxThreadsBox.mouseClicked(par1, par2, par3);
	}
	
	/**
	 * Draws the screen and all the components in it.
	 */
	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		drawDefaultBackground();
		drawCenteredString(fontRendererObj, "Server Finder", width / 2, 20,
			16777215);
		drawCenteredString(fontRendererObj,
			"This will search for servers with similar IPs", width / 2, 40,
			10526880);
		drawCenteredString(fontRendererObj,
			"to the IP you type into the field below.", width / 2, 50,
			10526880);
		drawCenteredString(fontRendererObj,
			"The servers it finds will be added to your server list.",
			width / 2, 60, 10526880);
		drawString(fontRendererObj, "Server address:", width / 2 - 100,
			height / 4 + 24, 10526880);
		ipBox.drawTextBox();
		drawString(fontRendererObj, "Max. threads:", width / 2 - 100,
			height / 4 + 60, 10526880);
		maxThreadsBox.drawTextBox();
		
		drawCenteredString(fontRendererObj, state.toString(), width / 2,
			height / 4 + 73, 10526880);
		
		drawString(fontRendererObj, "Checked: " + checked + " / 1792",
			width / 2 - 100, height / 4 + 84, 10526880);
		drawString(fontRendererObj, "Working: " + working, width / 2 - 100,
			height / 4 + 94, 10526880);
		super.drawScreen(par1, par2, par3);
	}
}
