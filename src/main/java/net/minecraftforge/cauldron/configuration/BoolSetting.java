package net.minecraftforge.cauldron.configuration;

import net.minecraftforge.cauldron.configuration.Setting;

import org.apache.commons.lang.BooleanUtils;

public class BoolSetting extends Setting<Boolean>
{
    Boolean value;
    public BoolSetting(String path, Boolean def, String description)
    {
        super(path, def, description);
        this.value = def;
    }

    @Override
    public Boolean getValue()
    {
        return value;
    }

    @Override
    public void setValue(String value)
    {
        this.value = BooleanUtils.toBooleanObject(value);
        this.value = this.value == null ? def : this.value;
        CauldronConfig.config.set(path, this.value);
    }
}
