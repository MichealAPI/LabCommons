package it.mikeslab.commons.api.inventory.util.action;

import com.google.common.collect.Multimap;
import it.mikeslab.commons.api.inventory.pojo.action.GuiAction;

public interface ActionRegistrar {

    /**
     * Load all the actions
     */
    Multimap<String, GuiAction> loadActions();


}
