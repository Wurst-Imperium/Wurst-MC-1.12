/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.ai;

import net.minecraft.util.math.BlockPos;

public class PathPos extends BlockPos
{
	private final boolean jumping;
	
	public PathPos(BlockPos pos)
	{
		this(pos, false);
	}
	
	public PathPos(BlockPos pos, boolean jumping)
	{
		super(pos);
		this.jumping = jumping;
	}
	
	public boolean isJumping()
	{
		return jumping;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(this == obj)
			return true;
		
		if(!(obj instanceof PathPos))
			return false;
		
		PathPos node = (PathPos)obj;
		return getX() == node.getX() && getY() == node.getY()
			&& getZ() == node.getZ() && isJumping() == node.isJumping();
	}
	
	@Override
	public int hashCode()
	{
		return super.hashCode() * 2 + (isJumping() ? 1 : 0);
	}
}
