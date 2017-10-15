package net.wurstclient.compatibility;

import java.io.IOException;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class CPacketNewKeepAlive implements Packet<INetHandlerPlayServer>
{
	private long key;
	
	public CPacketNewKeepAlive()
	{}
	
	public CPacketNewKeepAlive(long idIn)
	{
		key = idIn;
	}
	
	/**
	 * Passes this Packet on to the NetHandler for processing.
	 */
	@Override
	public void processPacket(INetHandlerPlayServer handler)
	{
		
	}
	
	/**
	 * Reads the raw packet data from the data stream.
	 */
	@Override
	public void readPacketData(PacketBuffer buf) throws IOException
	{
		key = buf.readLong();
	}
	
	/**
	 * Writes the raw packet data to the data stream.
	 */
	@Override
	public void writePacketData(PacketBuffer buf) throws IOException
	{
		buf.writeLong(key);
	}
	
	public long getKey()
	{
		return key;
	}
}
