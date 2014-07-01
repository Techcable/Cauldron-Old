package net.minecraftforge.cauldron.configuration;

import net.minecraftforge.cauldron.configuration.Setting;

public class IntSetting extends Setting<Integer>
{
    private Integer value;
    
    public IntSetting(String path, Integer def, String description)
    {
        super(path, def, description);
        this.value = def;
    }

    @Override
    public Integer getValue()
    {
        return value;
    }

    @Override
    public void setValue(String value)
    {
        this.value = org.apache.commons.lang.math.NumberUtils.toInt(value, def);
        CauldronConfig.config.set(path, this.value);
    }
}

