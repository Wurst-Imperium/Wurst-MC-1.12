package net.wurstclient.features.mods.render;

import net.wurstclient.features.Category;
import net.wurstclient.features.Mod;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;

@Mod.Bypasses
public final class NoWeatherMod extends Mod
{
	private final CheckboxSetting disableRain =
		new CheckboxSetting("Disable Rain", true);
	
	private final CheckboxSetting changeTime =
		new CheckboxSetting("Change World Time", false)
		{
			@Override
			public void update()
			{
				time.setDisabled(!isChecked());
			}
		};
	private final SliderSetting time =
		new SliderSetting("Time", 6000, 0, 23900, 100, ValueDisplay.INTEGER);
	
	private final CheckboxSetting changeMoonPhase =
		new CheckboxSetting("Change Moon Phase", false)
		{
			@Override
			public void update()
			{
				moonPhase.setDisabled(!isChecked());
			}
		};
	private final SliderSetting moonPhase =
		new SliderSetting("Moon Phase", 0, 0, 7, 1, ValueDisplay.INTEGER);
	
	public NoWeatherMod()
	{
		super("NoWeather",
			"Allows you to alter the client-side weather, time and moon phase.");
		setCategory(Category.RENDER);
	}
	
	@Override
	public void initSettings()
	{
		addSetting(disableRain);
		addSetting(changeTime);
		addSetting(time);
		addSetting(changeMoonPhase);
		addSetting(moonPhase);
	}
	
	public boolean isRainDisabled()
	{
		return isActive() && disableRain.isChecked();
	}
	
	public boolean isTimeChanged()
	{
		return isActive() && changeTime.isChecked();
	}
	
	public long getChangedTime()
	{
		return time.getValueI();
	}
	
	public boolean isMoonPhaseChanged()
	{
		return isActive() && changeMoonPhase.isChecked();
	}
	
	public int getChangedMoonPhase()
	{
		return moonPhase.getValueI();
	}
}
