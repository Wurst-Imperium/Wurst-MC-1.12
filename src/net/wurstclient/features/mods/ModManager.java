/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Comparator;
import java.util.TreeMap;

import net.wurstclient.features.Mod;
import net.wurstclient.features.mods.blocks.*;
import net.wurstclient.features.mods.chat.AntiSpamMod;
import net.wurstclient.features.mods.chat.FancyChatMod;
import net.wurstclient.features.mods.chat.ForceOpMod;
import net.wurstclient.features.mods.chat.HomeMod;
import net.wurstclient.features.mods.chat.MassTpaMod;
import net.wurstclient.features.mods.chat.SpammerMod;
import net.wurstclient.features.mods.combat.*;
import net.wurstclient.features.mods.fun.DerpMod;
import net.wurstclient.features.mods.fun.HeadRollMod;
import net.wurstclient.features.mods.fun.HeadlessMod;
import net.wurstclient.features.mods.fun.LsdMod;
import net.wurstclient.features.mods.fun.MileyCyrusMod;
import net.wurstclient.features.mods.fun.SkinDerpMod;
import net.wurstclient.features.mods.fun.TiredMod;
import net.wurstclient.features.mods.items.CmdBlockMod;
import net.wurstclient.features.mods.items.CrashChestMod;
import net.wurstclient.features.mods.items.CrashTagMod;
import net.wurstclient.features.mods.items.KillPotionMod;
import net.wurstclient.features.mods.items.TrollPotionMod;
import net.wurstclient.features.mods.movement.*;
import net.wurstclient.features.mods.other.*;
import net.wurstclient.features.mods.render.*;
import net.wurstclient.features.mods.retro.AntiFireMod;
import net.wurstclient.features.mods.retro.AntiPotionMod;
import net.wurstclient.features.mods.retro.FastBowMod;
import net.wurstclient.features.mods.retro.FastEatMod;
import net.wurstclient.features.mods.retro.ForcePushMod;
import net.wurstclient.features.mods.retro.RegenMod;

public final class ModManager
{
	private final TreeMap<String, Mod> mods =
		new TreeMap<>(new Comparator<String>()
		{
			@Override
			public int compare(String o1, String o2)
			{
				return o1.compareToIgnoreCase(o2);
			}
		});
	
	public final AntiAfkMod antiAfkMod = new AntiAfkMod();
	public final AntiBlindMod antiBlindMod = new AntiBlindMod();
	public final AntiCactusMod antiCactusMod = new AntiCactusMod();
	public final AntiFireMod antiFireMod = new AntiFireMod();
	public final AntiKnockbackMod antiKnockbackMod = new AntiKnockbackMod();
	public final AntiPotionMod antiPotionMod = new AntiPotionMod();
	public final AntiSpamMod antiSpamMod = new AntiSpamMod();
	public final AutoArmorMod autoArmorMod = new AutoArmorMod();
	public final AutoBuildMod autoBuildMod = new AutoBuildMod();
	public final AutoLeaveMod autoLeaveMod = new AutoLeaveMod();
	public final AutoEatMod autoEatMod = new AutoEatMod();
	public final AutoFishMod autoFishMod = new AutoFishMod();
	public final AutoMineMod autoMineMod = new AutoMineMod();
	public final AutoRespawnMod autoRespawnMod = new AutoRespawnMod();
	public final AutoSignMod autoSignMod = new AutoSignMod();
	public final AutoSplashPotMod autoSplashPotMod = new AutoSplashPotMod();
	public final AutoSoupMod autoSoupMod = new AutoSoupMod();
	public final AutoSprintMod autoSprintMod = new AutoSprintMod();
	public final AutoStealMod autoStealMod = new AutoStealMod();
	public final AutoSwitchMod autoSwitchMod = new AutoSwitchMod();
	public final AutoSwordMod autoSwordMod = new AutoSwordMod();
	public final AutoToolMod autoToolMod = new AutoToolMod();
	public final AutoWalkMod autoWalkMod = new AutoWalkMod();
	public final BaseFinderMod baseFinderMod = new BaseFinderMod();
	public final BlinkMod blinkMod = new BlinkMod();
	public final BoatFlyMod boatFlyMod = new BoatFlyMod();
	public final BonemealAuraMod bonemealAuraMod = new BonemealAuraMod();
	public final BowAimbotMod bowAimbotMod = new BowAimbotMod();
	public final BuildRandomMod buildRandomMod = new BuildRandomMod();
	public final BunnyHopMod bunnyHopMod = new BunnyHopMod();
	public final CameraNoClipMod cameraNoClipMod = new CameraNoClipMod();
	public final CaveFinderMod caveFinderMod = new CaveFinderMod();
	public final ChestEspMod chestEspMod = new ChestEspMod();
	public final ClickAuraMod clickAuraMod = new ClickAuraMod();
	public final ClickGuiMod clickGuiMod = new ClickGuiMod();
	public final CmdBlockMod cmdBlockMod = new CmdBlockMod();
	public final CrashChestMod crashChestMod = new CrashChestMod();
	public final CrashTagMod crashTagMod = new CrashTagMod();
	public final CriticalsMod criticalsMod = new CriticalsMod();
	public final DerpMod derpMod = new DerpMod();
	public final DolphinMod dolphinMod = new DolphinMod();
	public final ExtraElytraMod extraElytraMod = new ExtraElytraMod();
	public final FancyChatMod fancyChatMod = new FancyChatMod();
	public final FastBreakMod fastBreakMod = new FastBreakMod();
	public final FastBowMod fastBowMod = new FastBowMod();
	public final FastEatMod fastEatMod = new FastEatMod();
	public final FastLadderMod fastLadderMod = new FastLadderMod();
	public final FastPlaceMod fastPlaceMod = new FastPlaceMod();
	public final FightBotMod fightBotMod = new FightBotMod();
	public final FlightMod flightMod = new FlightMod();
	public final FollowMod followMod = new FollowMod();
	public final ForceOpMod forceOpMod = new ForceOpMod();
	public final ForcePushMod forcePushMod = new ForcePushMod();
	public final FreecamMod freecamMod = new FreecamMod();
	public final FullbrightMod fullbrightMod = new FullbrightMod();
	public final GhostHandMod ghostHandMod = new GhostHandMod();
	public final GlideMod glideMod = new GlideMod();
	public final HeadlessMod headlessMod = new HeadlessMod();
	public final HeadRollMod headRollMod = new HeadRollMod();
	public final HealthTagsMod healthTagsMod = new HealthTagsMod();
	public final HighJumpMod highJumpMod = new HighJumpMod();
	public final HomeMod homeMod = new HomeMod();
	public final InstantBunkerMod instantBunkerMod = new InstantBunkerMod();
	public final InvWalkMod invWalkMod = new InvWalkMod();
	public final ItemEspMod itemEspMod = new ItemEspMod();
	public final JesusMod jesusMod = new JesusMod();
	public final JetpackMod jetpackMod = new JetpackMod();
	public final KaboomMod kaboomMod = new KaboomMod();
	public final KillauraLegitMod killauraLegitMod = new KillauraLegitMod();
	public final KillauraMod killauraMod = new KillauraMod();
	public final KillPotionMod killPotionMod = new KillPotionMod();
	public final LiquidsMod liquidsMod = new LiquidsMod();
	public final LogSpammerMod logSpammerMod = new LogSpammerMod();
	public final LsdMod lsdMod = new LsdMod();
	public final MassTpaMod massTpaMod = new MassTpaMod();
	public final MileyCyrusMod mileyCyrusMod = new MileyCyrusMod();
	public final MobEspMod mobEspMod = new MobEspMod();
	public final MultiAuraMod multiAuraMod = new MultiAuraMod();
	public final NameProtectMod nameProtectMod = new NameProtectMod();
	public final NameTagsMod nameTagsMod = new NameTagsMod();
	public final NavigatorMod navigatorMod = new NavigatorMod();
	public final NoClipMod noClipMod = new NoClipMod();
	public final NoFallMod noFallMod = new NoFallMod();
	public final NoHurtcamMod noHurtcamMod = new NoHurtcamMod();
	public final NoOverlayMod noOverlayMod = new NoOverlayMod();
	public final NoSlowdownMod noSlowdownMod = new NoSlowdownMod();
	public final NoWeatherMod noWeatherMod = new NoWeatherMod();
	public final NoWebMod noWebMod = new NoWebMod();
	public final NukerMod nukerMod = new NukerMod();
	public final NukerLegitMod nukerLegitMod = new NukerLegitMod();
	public final OverlayMod overlayMod = new OverlayMod();
	public final PanicMod panicMod = new PanicMod();
	public final ParkourMod parkourMod = new ParkourMod();
	public final PhaseMod phaseMod = new PhaseMod();
	public final PlayerEspMod playerEspMod = new PlayerEspMod();
	public final PlayerFinderMod playerFinderMod = new PlayerFinderMod();
	public final PotionSaverMod potionSaverMod = new PotionSaverMod();
	public final ProphuntEspMod prophuntEspMod = new ProphuntEspMod();
	public final ProtectMod protectMod = new ProtectMod();
	public final RegenMod regenMod = new RegenMod();
	public final RemoteViewMod remoteViewMod = new RemoteViewMod();
	public final SafeWalkMod safeWalkMod = new SafeWalkMod();
	public final ScaffoldWalkMod scaffoldWalkMod = new ScaffoldWalkMod();
	public final SearchMod searchMod = new SearchMod();
	public final SkinDerpMod skinDerpMod = new SkinDerpMod();
	public final SneakMod sneakMod = new SneakMod();
	public final SpammerMod spammerMod = new SpammerMod();
	public final SpeedHackMod speedHackMod = new SpeedHackMod();
	public final SpeedNukerMod speedNukerMod = new SpeedNukerMod();
	public final SpiderMod spiderMod = new SpiderMod();
	public final StepMod stepMod = new StepMod();
	public final TemplateToolMod templateToolMod = new TemplateToolMod();
	public final ThrowMod throwMod = new ThrowMod();
	public final TimerMod timerMod = new TimerMod();
	public final TiredMod tiredMod = new TiredMod();
	public final TracersMod tracersMod = new TracersMod();
	public final TpAuraMod tpAuraMod = new TpAuraMod();
	public final TrajectoriesMod trajectoriesMod = new TrajectoriesMod();
	public final TriggerBotMod triggerBotMod = new TriggerBotMod();
	public final TrollPotionMod trollPotionMod = new TrollPotionMod();
	public final TrueSightMod trueSightMod = new TrueSightMod();
	public final TunnellerMod tunnellerMod = new TunnellerMod();
	public final XRayMod xRayMod = new XRayMod();
	
	public ModManager()
	{
		try
		{
			for(Field field : ModManager.class.getFields())
				if(field.getName().endsWith("Mod"))
				{
					Mod mod = (Mod)field.get(this);
					mods.put(mod.getName(), mod);
					mod.initSettings();
				}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public Mod getModByName(String name)
	{
		return mods.get(name);
	}
	
	public Collection<Mod> getAllMods()
	{
		return mods.values();
	}
	
	public int countMods()
	{
		return mods.size();
	}
}
