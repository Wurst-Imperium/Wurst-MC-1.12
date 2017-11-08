package net.wurstclient.compatibility;

import java.util.Collections;
import java.util.NavigableMap;
import java.util.TreeMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;

public final class WMinecraft
{
	public static final String VERSION = "1.12";
	public static final String DISPLAY_VERSION = "1.12";
	public static final boolean OPTIFINE = false;
	public static final boolean REALMS = false;
	public static final boolean COOLDOWN = true;
	
	public static final NavigableMap<Integer, String> PROTOCOLS;
	static
	{
		TreeMap<Integer, String> protocols = new TreeMap<>();
		protocols.put(335, "1.12");
		PROTOCOLS = Collections.unmodifiableNavigableMap(protocols);
	}
	
	private static final Minecraft mc = Minecraft.getMinecraft();
	
	public static EntityPlayerSP getPlayer()
	{
		return mc.player;
	}
	
	public static WorldClient getWorld()
	{
		return mc.world;
	}
	
	public static NetHandlerPlayClient getConnection()
	{
		return getPlayer().connection;
	}
	
	public static boolean isRunningOnMac()
	{
		return Minecraft.IS_RUNNING_ON_MAC;
	}
}
