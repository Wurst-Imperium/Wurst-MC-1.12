package net.wurstclient.compatibility;

import java.io.IOException;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class SPacketNewKeepAlive implements Packet<INetHandlerPlayClient>
{
	private long id;
	
	public SPacketNewKeepAlive()
	{}
	
	public SPacketNewKeepAlive(long idIn)
	{
		id = idIn;
	}
	
	/**
	 * Passes this Packet on to the NetHandler for processing.
	 */
	@Override
	public void processPacket(INetHandlerPlayClient handler)
	{
		handler.handleNewKeepAlive(this);
	}
	
	/**
	 * Reads the raw packet data from the data stream.
	 */
	@Override
	public void readPacketData(PacketBuffer buf) throws IOException
	{
		id = buf.readLong();
	}
	
	/**
	 * Writes the raw packet data to the data stream.
	 */
	@Override
	public void writePacketData(PacketBuffer buf) throws IOException
	{
		buf.writeLong(id);
	}
	
	public long getId()
	{
		return id;
	}
}
