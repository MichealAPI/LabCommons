package it.mikeslab.commons.api.config;

/**
 * Interface for config-oriented enums
 */
public interface ConfigurableEnum {

    /**
     * Get the path of the configuration
     * @return the path
     */
    String getPath();

    /**
     * Get the default value of the configuration
     * @return the default value
     */
    Object getDefaultValue();

}
