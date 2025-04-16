package it.mikeslab.commons.api.database.pojo;

import it.mikeslab.commons.api.database.SerializableMapConvertible;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TestObject implements SerializableMapConvertible<TestObject> {

    private Map<TestEnum, String> values;

    public TestObject(String identifier) {
        this.values = new HashMap<>();
        this.values.put(TestEnum.TEST1, identifier);
    }

    @Override
    public TestObject fromMap(Map<String, Object> map) {

        TestObject testObject = new TestObject();

        testObject.values = new HashMap<>();

        for(Map.Entry<String, Object> entry : map.entrySet()) {

            testObject.values.put(
                    TestEnum.valueOf(entry.getKey()),
                    entry.getValue().toString()
            );
        }

        return testObject;
    }

    @Override
    public Map<String, Object> toMap() {

        Map<String, Object> map = new HashMap<>();

        for(Map.Entry<TestEnum, String> entry : values.entrySet()) {
            map.put(entry.getKey().name(), entry.getValue());
        }

        return map;
    }

    @Override
    public String getUniqueIdentifierName() {
        return TestEnum.TEST1.name();
    }

    @Override
    public Object getUniqueIdentifierValue() {
        return values.get(TestEnum.TEST1);
    }

    @Override
    public Set<String> getIdentifiers() {

        TestEnum[] testEnums = TestEnum.values();

        Set<String> identifiers = new HashSet<>();

        for(TestEnum testEnum : testEnums) {

            boolean isPrimaryId = testEnum.name()
                    .equals(
                            this.getUniqueIdentifierName()
                    );

            if(isPrimaryId) continue;

            identifiers.add(testEnum.name());
        }

        return identifiers;
    }


    public enum TestEnum {
        TEST1, // Identifier
        TEST2,
        TEST3
    }

}
