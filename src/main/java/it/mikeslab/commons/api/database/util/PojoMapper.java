package it.mikeslab.commons.api.database.util;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.ApiStatus;

import java.util.Arrays;
import java.util.List;

@UtilityClass
public class PojoMapper {

    @Deprecated
    @ApiStatus.Experimental
    public String simpleSerializer(List<String> list) {
        return list.toString();
    }

    @ApiStatus.Experimental
    public List<String> simpleDeserializer(String string) {
        // Split the string by commas and whitespace, ignoring brackets
        return Arrays.asList(string.split("\\s*,\\s*"));
    }






}
