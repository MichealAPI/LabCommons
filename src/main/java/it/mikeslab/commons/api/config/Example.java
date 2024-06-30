package it.mikeslab.commons.api.config;

public enum Example implements ConfigurableEnum{
    ;

    @Override
    public String getPath() {
        return "";
    }

    @Override
    public Object getDefaultValue() {
        return null;
    }
}
