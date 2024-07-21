package it.mikeslab.commons.api.database.impl;

import it.mikeslab.commons.api.database.Database;
import it.mikeslab.commons.api.database.pojo.TestObject;
import it.mikeslab.commons.api.database.pojo.URIBuilder;
import it.mikeslab.commons.api.logger.LogUtils;
import org.junit.jupiter.api.*;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class SQLDatabaseTest {

    private Database<TestObject> db;

    private static String SQL_TEST_DB_URL,
            SQL_TEST_DB_USER,
            SQL_TEST_DB_PASSWORD,
            SQL_TEST_DB_TABLE;

    @BeforeEach
    public void setUp() {

        this.loadEnvironmentConfig();

        URIBuilder uriBuilder = URIBuilder
                .builder()
                .uri(SQL_TEST_DB_URL)
                .username(SQL_TEST_DB_USER)
                .password(SQL_TEST_DB_PASSWORD)
                .table(SQL_TEST_DB_TABLE)
                .isSqlite(false)
                .build();


        db = new SQLDatabaseImpl<>(uriBuilder);
        db.connect(new TestObject());
    }

    @AfterEach
    public void tearDown() {
        db.disconnect();
    }

    @Test
    @Order(1)
    public void testConnect() {
        Assertions.assertNotNull(db);
    }

    @Test
    @Order(2)
    public void testUpsert() {
        
        Map<TestObject.TestEnum, String> testValues = this.getTestValues();
        
        TestObject testObject = new TestObject(testValues);
        
        boolean result = db.upsert(testObject);

        LogUtils.log(
                Level.INFO,
                LogUtils.LogSource.TEST,
                "Upsert result: " + (result ? "SUCCESS" : "FAILURE")
        );
        
    }
    
    @Test
    @Order(3)
    public void testFindOne() {

        Map<TestObject.TestEnum, String> testValues = this.getTestValues();
        
        // TEST1 stands for the identifier
        TestObject filter = new TestObject(
                testValues.get(
                        TestObject.TestEnum.TEST1
                )
        );

        LogUtils.log(
                Level.INFO,
                LogUtils.LogSource.TEST,
                "Finding object with identifier: " + filter
                        .getValues()
                        .get(TestObject.TestEnum.TEST1)
        );
        
        TestObject result = db.findOne(filter);
        
        Assertions.assertNotNull(result);
        Assertions.assertEquals(
                testValues.get(TestObject.TestEnum.TEST2),
                result.getValues().get(TestObject.TestEnum.TEST2)
        );

        LogUtils.log(
                Level.INFO,
                LogUtils.LogSource.TEST,
                "Found object: " + result
                        .getValues()
                        .toString()
        );
        
    }


    /**
     * Helper method to get test values
     * @return Map<TestObject.TestEnum, String>
     */
    private Map<TestObject.TestEnum, String> getTestValues() {
        
        Map<TestObject.TestEnum, String> testValues = new HashMap<>();
        
        testValues.put(TestObject.TestEnum.TEST1, "test1");
        testValues.put(TestObject.TestEnum.TEST2, "test2");
        testValues.put(TestObject.TestEnum.TEST3, "test3");
        
        return testValues;
        
    }


    /**
     * Load environment configuration from a YAML file
     * that is excluded from the repository and contains
     * sensitive information
     */
    public void loadEnvironmentConfig() {

        Yaml yaml = new Yaml();
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("environment.yaml");

        Map<String, Object> envConfig = yaml.load(inputStream);

        SQL_TEST_DB_URL = String.valueOf(envConfig.get("SQL_TEST_DB_URL"));
        SQL_TEST_DB_USER = String.valueOf(envConfig.get("SQL_TEST_DB_USER"));
        SQL_TEST_DB_PASSWORD = String.valueOf(envConfig.get("SQL_TEST_DB_PASSWORD"));
        SQL_TEST_DB_TABLE = String.valueOf(envConfig.get("SQL_TEST_DB_TABLE"));

    }



}
