package it.mikeslab.widencommons.api.inventory.util;

import it.mikeslab.widencommons.api.inventory.GuiType;
import it.mikeslab.widencommons.api.inventory.pojo.GuiDetails;
import it.mikeslab.widencommons.api.inventory.pojo.GuiElement;
import it.mikeslab.widencommons.api.logger.LoggerUtil;
import lombok.experimental.UtilityClass;

import java.util.Map;
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
            LoggerUtil.log(
                    Level.WARNING,
                    LoggerUtil.LogSource.API,
                    "Invalid size (must be a multiple of 9 and less than 54)"
            );
            flag = 1;
        }

        GuiType type = guiDetails.getGuiType();

        if(type == null) {
            LoggerUtil.log(
                    Level.WARNING,
                    LoggerUtil.LogSource.API,
                    "Null inventory type"
            );
            flag = 1;
        }

        Map<Character, GuiElement> elements = guiDetails.getElements();

        // If it's empty, inventory will just be empty
        if(elements == null) {
            LoggerUtil.log(
                    Level.WARNING,
                    LoggerUtil.LogSource.API,
                    "Null elements map"
            );
            flag = 1;
        }

        String[] layout = guiDetails.getInventoryLayout();

        // If the layout is not valid, we can't proceed
        if(flag == 0) {
            if(!elements.isEmpty() && isLayoutValid(layout)) {
                LoggerUtil.log(
                        Level.WARNING,
                        LoggerUtil.LogSource.API,
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

        boolean areEntriesValid = layout.length > 2 && layout.length < 7;
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
