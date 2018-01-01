/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.bot.commands;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public abstract class Command
{
	private String name = getClass().getAnnotation(Info.class).name();
	private String help = getClass().getAnnotation(Info.class).help();
	private String[] syntax = getClass().getAnnotation(Info.class).syntax();
	
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Info
	{
		String name();
		
		String help();
		
		String[] syntax();
	}
	
	public class CmdSyntaxError extends CmdError
	{
		public CmdSyntaxError()
		{
			super();
		}
		
		public CmdSyntaxError(String message)
		{
			super(message);
		}
	}
	
	public class CmdError extends Throwable
	{
		public CmdError()
		{
			super();
		}
		
		public CmdError(String message)
		{
			super(message);
		}
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getHelp()
	{
		return help;
	}
	
	public String[] getSyntax()
	{
		return syntax;
	}
	
	public void printHelp()
	{
		for(String line : help.split("\n"))
			System.out.println(line);
	}
	
	public void printSyntax()
	{
		System.out.println("Syntax:");
		String output = name;
		if(syntax.length != 0)
		{
			output += " ";
			String spaces = "";
			while(spaces.length() < output.length())
				spaces += " ";
			output += syntax[0];
			for(int i = 1; i < syntax.length; i++)
				output += "\n" + spaces + syntax[i];
		}
		for(String line : output.split("\n"))
			System.out.println(line);
	}
	
	protected final void syntaxError() throws CmdSyntaxError
	{
		throw new CmdSyntaxError();
	}
	
	protected final void syntaxError(String message) throws CmdSyntaxError
	{
		throw new CmdSyntaxError(message);
	}
	
	protected final void error(String message) throws CmdError
	{
		throw new CmdError(message);
	}
	
	public abstract void execute(String[] args) throws CmdError;
}
