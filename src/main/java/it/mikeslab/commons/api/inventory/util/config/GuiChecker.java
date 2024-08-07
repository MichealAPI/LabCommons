package it.mikeslab.commons.api.inventory.util.config;

import com.google.common.collect.Multimap;
import it.mikeslab.commons.api.inventory.GuiType;
import it.mikeslab.commons.api.inventory.pojo.GuiDetails;
import it.mikeslab.commons.api.inventory.pojo.GuiElement;
import it.mikeslab.commons.api.logger.LogUtils;
import lombok.experimental.UtilityClass;

import java.util.logging.Level;

@UtilityClass
public class GuiChecker {

    public boolean isSizeValid(GuiDetails guiDetails) {

        int size = guiDetails.getInventorySize();

        GuiType type = guiDetails.getGuiType();
        int rowLength = type.getRowLength();


        return size % rowLength == 0 && size <= 54;
    }


    /**
     * Note:
     *      By checking params validity using flags,
     *      we can list all the errors at once
     *
     * @param guiDetails Custom Gui Details
     * @return details validity
     */
    public boolean isValid(GuiDetails guiDetails) {

        // Flag for validation
        int flag = 0;

        // parsing details

        if(!GuiChecker.isSizeValid(guiDetails)) {
            LogUtils.warn(
                    LogUtils.LogSource.API,
                    "Invalid size (must be a multiple of 9 and less than 54)"
            );
            flag = 1;
        }

        GuiType type = guiDetails.getGuiType();

        if(type == null) {
            LogUtils.warn(
                    LogUtils.LogSource.API,
                    "Null inventory type"
            );
            flag = 1;
        }

        Multimap<Character, GuiElement> elements = guiDetails.getElements();

        // If it's empty, inventory will just be empty
        if(elements == null) {
            LogUtils.warn(
                    LogUtils.LogSource.API,
                    "Null elements map"
            );
            flag = 1;
        }

        String[] layout = guiDetails.getInventoryLayout();

        // If the layout is not valid, we can't proceed
        if(flag == 0) {
            if(!elements.isEmpty() && isLayoutValid(layout)) {
                LogUtils.log(
                        Level.WARNING,
                        LogUtils.LogSource.API,
                        "Invalid layout"
                );
                flag = 1;
            }
        }

        return flag == 0;
    }


    /**
     * Check if the layout is valid, i.e., if the rows have the correct length
     * @param layout Inventory layout
     * @return layout validity
     */
    public boolean isLayoutValid(String[] layout) {

        boolean areEntriesValid = layout.length > 0 && layout.length < 7;
        boolean areRowsValid = true;

        for(String row : layout) {
            if(row.length() % 3 != 0) {
                areRowsValid = false;
                break;
            }
        }

        return !areEntriesValid || !areRowsValid;

    }









}
