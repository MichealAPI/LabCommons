package it.mikeslab.commons.api.various.platform;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.ApiStatus;

@UtilityClass
public class PlatformUtil {

    private final String[]
             spigotOnlyClasses = {"org.spigotmc.SpigotConfig"},
             paperOnlyClasses = {"io.papermc.paper.configuration.Configuration",
                                 "com.destroystokyo.paper.PaperConfig"
                                };

    private Platform CACHED_PLATFORM;

    public Platform getPlatform() {
        if(isPaper()) {
            return Platform.PAPER;
        } else if(isSpigot()) {
            return Platform.SPIGOT;
        } else {
            return Platform.UNKNOWN;
        }
    }

    public boolean isPaper() {

        if(CACHED_PLATFORM != null) {
            return CACHED_PLATFORM == Platform.PAPER;
        }

        boolean isPaper = checkClasses(paperOnlyClasses);
        updateCache(Platform.PAPER, isPaper);

        return isPaper;
    }

    public boolean isSpigot() {

        if(CACHED_PLATFORM != null) {
            return CACHED_PLATFORM == Platform.SPIGOT;
        }

        boolean isSpigot = checkClasses(spigotOnlyClasses);
        updateCache(Platform.SPIGOT, isSpigot);

        return isSpigot;
    }

    public boolean isUnknown() {

        if(CACHED_PLATFORM != null) {
            return CACHED_PLATFORM == Platform.UNKNOWN;
        }

        boolean isUnknown = !isSpigot() && !isPaper();
        updateCache(Platform.UNKNOWN, isUnknown);

        return isUnknown;
    }

    /**
     * Experimental method to get the platform from a class name
     * @param className the class name to check
     * @return the platform
     */
    @ApiStatus.Experimental
    public Platform fromClass(String className) {

        if(className.contains(Platform.PAPER.name())) {
            return Platform.PAPER;
        } else if(className.contains(Platform.SPIGOT.name())) {
            return Platform.SPIGOT;
        }

        return Platform.UNKNOWN;
    }


    private void updateCache(Platform platform, boolean isPlatform) {
        CACHED_PLATFORM = isPlatform ? platform : null;
    }

    private boolean checkClasses(String[] classes) {
        for (String className : classes) {
            try {
                Class.forName(className);
                return true;
            } catch (ClassNotFoundException e) {
                continue;
            }
        }
        return false;
    }

}
