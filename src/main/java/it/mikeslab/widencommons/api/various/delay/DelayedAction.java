package it.mikeslab.widencommons.api.various.delay;

public interface DelayedAction {

    /**
     * The default delay for the action
     * expressed in milliseconds
     * @return the default delay
     */
    long getDefaultDelay();

    /**
     * The bypass permission for the action
     * @return the bypass permission
     */
    String getBypassPermission();

}
