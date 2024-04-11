package it.mikeslab.widencommons;

import it.mikeslab.widencommons.api.lang.ConfigurableEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Test implements ConfigurableEnum {

    TEST("test", "test default");

    private final String path;
    private final String defaultValue;

}
