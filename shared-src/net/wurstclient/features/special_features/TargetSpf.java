/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.special_features;

import net.wurstclient.features.Feature;
import net.wurstclient.features.HelpPage;
import net.wurstclient.features.SearchTags;
import net.wurstclient.features.Spf;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.ColorsSetting;

@HelpPage("Special_Features/Target")
@SearchTags({"AntiBot", "anti bot", "AntiKillauraBot", "anti killaura bot"})
public final class TargetSpf extends Spf
{
	public final CheckboxSetting players = new CheckboxSetting("Players", true);
	public final CheckboxSetting animals = new CheckboxSetting("Animals", true);
	public final CheckboxSetting monsters =
		new CheckboxSetting("Monsters", true);
	public final CheckboxSetting golems = new CheckboxSetting("Golems", true);
	
	public final CheckboxSetting sleepingPlayers =
		new CheckboxSetting("Sleeping players", false);
	public final CheckboxSetting invisiblePlayers =
		new CheckboxSetting("Invisible players", false);
	public final CheckboxSetting invisibleMobs =
		new CheckboxSetting("Invisible mobs", false);
	
	public final CheckboxSetting teams = new CheckboxSetting("Teams", false);
	public final ColorsSetting teamColors = new ColorsSetting("Team Colors",
		new boolean[]{true, true, true, true, true, true, true, true, true,
			true, true, true, true, true, true, true});
	
	public TargetSpf()
	{
		super("Target",
			"Controls what entities are targeted by other features (e.g. Killaura). Also allows you to\n"
				+ "bypass AntiAura plugins by filtering out fake entities.");
		
		addSetting(players);
		addSetting(animals);
		addSetting(monsters);
		addSetting(golems);
		
		addSetting(sleepingPlayers);
		addSetting(invisiblePlayers);
		addSetting(invisibleMobs);
		
		addSetting(teams);
		addSetting(teamColors);
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.mods.killauraMod,
			wurst.mods.killauraLegitMod, wurst.mods.multiAuraMod,
			wurst.mods.clickAuraMod, wurst.mods.triggerBotMod,
			wurst.mods.bowAimbotMod};
	}
}
