package it.mikeslab.widencommons.api.database.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;

import java.util.Map;

@UtilityClass
public class PojoMapper {

    /**
     * Convert a POJO to a map
     * @param pojoClass the class of the POJO
     * @return the map
     */
    public Map<String, Object> toMap(Object pojoClass) {

        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.convertValue(
                pojoClass,
                new TypeReference<>() {}
        );
    }

    /**
     * Convert a map to a POJO
     * @param map the map
     * @param pojoClass the class of the POJO
     * @return the POJO
     */
    public <T> T fromMap(Map<String, Object> map, Class<T> pojoClass) {

        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.convertValue(
                map,
                pojoClass
        );
    }











}
