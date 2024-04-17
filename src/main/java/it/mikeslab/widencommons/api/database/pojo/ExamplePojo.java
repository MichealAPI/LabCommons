package it.mikeslab.widencommons.api.database.pojo;

import it.mikeslab.widencommons.api.database.SerializableMapConvertible;
import it.mikeslab.widencommons.api.database.util.PojoMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamplePojo implements SerializableMapConvertible<ExamplePojo> {

    private String test;
    private List<String> testList;


    @Override
    public ExamplePojo fromMap(Map<String, Object> map) {

        this.test = (String) map.get("test");
        this.testList = PojoMapper.simpleDeserializer((String) map.get("testList"));

        return this;
    }

    @Override
    public Map<String, Object> toMap() {

        Map<String, Object> conversionMap = new HashMap<>(2); // Initialize with expected size

        if(test == null) {
            throw new IllegalStateException("test cannot be null");
        }

        conversionMap.put("test", test);

        if(testList != null) {
            String serializedTestList = String.join(", ", testList);
            conversionMap.put("testList", serializedTestList);
        }

        return conversionMap;
    }

    public ExamplePojo filter(String test) {
        return new ExamplePojo(test, null);
    }
}
