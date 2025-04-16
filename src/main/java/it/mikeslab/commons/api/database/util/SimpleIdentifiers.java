package it.mikeslab.commons.api.database.util;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public interface SimpleIdentifiers {

    String getKey();

    Class<?> getType();

    static Set<String> identifiers(Class<? extends SimpleIdentifiers> clazz) {
        return Arrays.stream(clazz.getEnumConstants())
                .map(SimpleIdentifiers::getKey)
                .collect(Collectors.toSet());
    }

}
