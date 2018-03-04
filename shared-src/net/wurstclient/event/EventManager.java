/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.event;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;
import net.wurstclient.WurstClient;
import net.wurstclient.events.*;

public final class EventManager
{
	private final HashMap<Class<? extends Listener>, ArrayList<? extends Listener>> listenerMap =
		new HashMap<>();
	
	{
		listenerMap.put(ChatInputListener.class,
			new ArrayList<ChatInputListener>());
		listenerMap.put(ChatOutputListener.class,
			new ArrayList<ChatOutputListener>());
		listenerMap.put(DeathListener.class, new ArrayList<DeathListener>());
		listenerMap.put(GUIRenderListener.class,
			new ArrayList<GUIRenderListener>());
		listenerMap.put(LeftClickListener.class,
			new ArrayList<LeftClickListener>());
		listenerMap.put(RightClickListener.class,
			new ArrayList<RightClickListener>());
		listenerMap.put(KeyPressListener.class,
			new ArrayList<KeyPressListener>());
		listenerMap.put(PacketInputListener.class,
			new ArrayList<PacketInputListener>());
		listenerMap.put(PacketOutputListener.class,
			new ArrayList<PacketOutputListener>());
		listenerMap.put(RenderListener.class, new ArrayList<RenderListener>());
		listenerMap.put(UpdateListener.class, new ArrayList<UpdateListener>());
		listenerMap.put(PostUpdateListener.class,
			new ArrayList<PostUpdateListener>());
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Event> void fire(T event)
	{
		if(!WurstClient.INSTANCE.isEnabled())
			return;
		
		try
		{
			ArrayList<? extends Listener> listeners =
				listenerMap.get(event.getListenerType());
			
			event.fire(new ArrayList<>(listeners));
			
		}catch(Throwable e)
		{
			e.printStackTrace();
			
			CrashReport report =
				CrashReport.makeCrashReport(e, "Firing Wurst event");
			CrashReportCategory category =
				report.makeCategory("Affected event");
			category.setDetail("Event class", () -> event.getClass().getName());
			
			throw new ReportedException(report);
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Listener> void add(Class<T> type, T listener)
	{
		try
		{
			((ArrayList<T>)listenerMap.get(type)).add(listener);
			
		}catch(Throwable e)
		{
			e.printStackTrace();
			
			CrashReport report =
				CrashReport.makeCrashReport(e, "Adding Wurst event listener");
			CrashReportCategory category =
				report.makeCategory("Affected listener");
			category.setDetail("Listener type", () -> type.getName());
			category.setDetail("Listener class",
				() -> listener.getClass().getName());
			
			throw new ReportedException(report);
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Listener> void remove(Class<T> type, T listener)
	{
		try
		{
			((ArrayList<T>)listenerMap.get(type)).remove(listener);
			
		}catch(Throwable e)
		{
			e.printStackTrace();
			
			CrashReport report =
				CrashReport.makeCrashReport(e, "Removing Wurst event listener");
			CrashReportCategory category =
				report.makeCategory("Affected listener");
			category.setDetail("Listener type", () -> type.getName());
			category.setDetail("Listener class",
				() -> listener.getClass().getName());
			
			throw new ReportedException(report);
		}
	}
}
