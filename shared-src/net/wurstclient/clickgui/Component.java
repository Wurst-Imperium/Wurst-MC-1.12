/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.clickgui;

public abstract class Component
{
	private int x;
	private int y;
	private int width;
	private int height;
	
	private Window parent;
	
	public void handleMouseClick(int mouseX, int mouseY, int mouseButton)
	{
		
	}
	
	public abstract void render(int mouseX, int mouseY, float partialTicks);
	
	public abstract int getDefaultWidth();
	
	public abstract int getDefaultHeight();
	
	public int getX()
	{
		return x;
	}
	
	public void setX(int x)
	{
		if(this.x != x)
			invalidateParent();
		this.x = x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public void setY(int y)
	{
		if(this.y != y)
			invalidateParent();
		this.y = y;
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public void setWidth(int width)
	{
		if(this.width != width)
			invalidateParent();
		this.width = width;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public void setHeight(int height)
	{
		if(this.height != height)
			invalidateParent();
		this.height = height;
	}
	
	public Window getParent()
	{
		return parent;
	}
	
	public void setParent(Window parent)
	{
		this.parent = parent;
	}
	
	private void invalidateParent()
	{
		if(parent != null)
			parent.invalidate();
	}
}
