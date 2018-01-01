/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.blocks;

import net.minecraft.util.text.ITextComponent;
import net.wurstclient.features.Category;
import net.wurstclient.features.HelpPage;
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;

@SearchTags({"auto sign"})
@HelpPage("Mods/AutoSign")
@Mod.Bypasses(ghostMode = false,
	latestNCP = false,
	olderNCP = false,
	antiCheat = false)
@Mod.DontSaveState
public final class AutoSignMod extends Mod
{
	private ITextComponent[] signText;
	
	public AutoSignMod()
	{
		super("AutoSign",
			"Instantly writes whatever text you want on every sign you place. Once activated, you can\n"
				+ "write normally on one sign to specify the text for all other signs.");
		setCategory(Category.BLOCKS);
	}
	
	@Override
	public void onDisable()
	{
		signText = null;
	}
	
	public ITextComponent[] getSignText()
	{
		return signText;
	}
	
	public void setSignText(ITextComponent[] signText)
	{
		if(isActive() && this.signText == null)
			this.signText = signText;
	}
}
