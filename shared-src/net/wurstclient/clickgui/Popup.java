/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.clickgui;

public abstract class Popup
{
	private final Component owner;
	
	private int x;
	private int y;
	private int width;
	private int height;
	private boolean closing;
	
	public Popup(Component owner)
	{
		this.owner = owner;
	}
	
	public abstract void handleMouseClick(int mouseX, int mouseY,
		int mouseButton);
	
	public abstract void render(int mouseX, int mouseY);
	
	public abstract int getDefaultWidth();
	
	public abstract int getDefaultHeight();
	
	public Component getOwner()
	{
		return owner;
	}
	
	public int getX()
	{
		return x;
	}
	
	public void setX(int x)
	{
		this.x = x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public void setY(int y)
	{
		this.y = y;
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public void setWidth(int width)
	{
		this.width = width;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public void setHeight(int height)
	{
		this.height = height;
	}
	
	public boolean isClosing()
	{
		return closing;
	}
	
	public void close()
	{
		closing = true;
	}
}
