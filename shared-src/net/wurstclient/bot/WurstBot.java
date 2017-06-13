package net.wurstclient.bot;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import net.minecraft.client.main.Main;
import net.wurstclient.WurstClient;
import net.wurstclient.bot.commands.Command;
import net.wurstclient.bot.commands.CommandManager;

public class WurstBot
{
	private static boolean enabled = false;
	private static WurstBot wurstBot;
	private final CommandManager commandManager;
	
	public WurstBot()
	{
		commandManager = new CommandManager();
	}
	
	public static void main(String[] args)
	{
		System.out.println("Starting Wurst-Bot...");
		enabled = true;
		wurstBot = new WurstBot();
		Main.main(new String[]{"--version", "mcp", "--accessToken", "0",
			"--assetsDir", "assets", "--assetIndex", "1.8", "--userProperties",
			"{}"});
	}
	
	public void start()
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					WurstBot.this.run();
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}, "Wurst-Bot").start();
	}
	
	private void run() throws Exception
	{
		BufferedReader br =
			new BufferedReader(new InputStreamReader(System.in));
		System.out.println();
		System.out.println(
			"           +++++++++++++++++++++++++++++++++++++++++++++++           ");
		System.out.println(
			"       +++#++++##++++#+#+++++++#++######+++++######+#######+++       ");
		System.out.println(
			"     +++++#++++##++++#+#+++++++#++#+++++##++#++++++++++#++++++++     ");
		System.out.println(
			"    +++++++#++#++#++#++#+++++++#++#######++++######++++#+++++++++    ");
		System.out.println(
			"     ++++++#++#++#++#+++#+++++#+++#+++##+++++++++++#+++#++++++++     ");
		System.out.println(
			"       +++++##++++##+++++#####++++#+++++##+++######++++#++++++       ");
		System.out.println(
			"           +++++++++++++++++++++++++++++++++++++++++++++++           ");
		System.out.println();
		System.out.println("Wurst-Bot v" + WurstClient.VERSION);
		System.out.println("Type \"help\" for a list of commands.");
		while(true)
		{
			String input = br.readLine();
			String commandName = input.split(" ")[0];
			String[] args;
			if(input.contains(" "))
				args = input.substring(input.indexOf(" ") + 1).split(" ");
			else
				args = new String[0];
			Command command = commandManager.getCommandByName(commandName);
			if(command != null)
				try
				{
					command.execute(args);
				}catch(Command.CmdSyntaxError e)
				{
					if(e.getMessage() != null)
						System.err.println("Syntax error: " + e.getMessage());
					else
						System.err.println("Syntax error!");
					command.printSyntax();
				}catch(Command.CmdError e)
				{
					System.err.println(e.getMessage());
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			else
				System.err
					.println("\"" + commandName + "\" is not a valid command.");
		}
	}
	
	public static boolean isEnabled()
	{
		return enabled;
	}
	
	public static WurstBot getBot()
	{
		return wurstBot;
	}
	
	public CommandManager getCommandManager()
	{
		return commandManager;
	}
}
