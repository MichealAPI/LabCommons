package it.mikeslab.commons.api.various.platform;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Platform {

    UNKNOWN("Unknown"),
    SPIGOT("Spigot"),
    PAPER("Paper");

    private final String platformName;

}
