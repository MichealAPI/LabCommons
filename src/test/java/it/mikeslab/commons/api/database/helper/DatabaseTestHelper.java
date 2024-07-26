package it.mikeslab.commons.api.database.helper;

import com.google.common.base.Stopwatch;
import it.mikeslab.commons.api.database.Database;
import it.mikeslab.commons.api.database.impl.MongoDatabaseImpl;
import it.mikeslab.commons.api.database.impl.SQLDatabaseImpl;
import it.mikeslab.commons.api.database.pojo.TestObject;
import it.mikeslab.commons.api.database.pojo.URIBuilder;
import it.mikeslab.commons.api.logger.LogUtils;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

@RequiredArgsConstructor
public class DatabaseTestHelper {

    private final URIBuilder uriBuilder;
    private Database<TestObject> db;

    /**
     * Establish a connection with the target database
     */
    public void setUp() {

        if(uriBuilder.getUri().startsWith("mongodb")) {
            db = new MongoDatabaseImpl<>(uriBuilder);
        } else {
            db = new SQLDatabaseImpl<>(uriBuilder);
        }

        db.connect(new TestObject());
    }

    public void tearDown() {
        db.disconnect();
    }

    public boolean isConnectionValid() {
        return db != null;
    }

    public void deleteIfExists() {

        boolean deleted = db.delete(this.getFilter()); //.join();

        LogUtils.log(
                Level.INFO,
                LogUtils.LogSource.TEST,
                "Is entry been deleted? " + (deleted ? "YES" : "NO")
        );

    }


    public void testUpsert() {


        Map<TestObject.TestEnum, String> testValues = this.getTestValues();
        
        TestObject testObject = new TestObject(testValues);

        Stopwatch timer = Stopwatch.createStarted();

        boolean result = db.upsert(testObject); //.join();

        timer.stop();

        LogUtils.info(
                LogUtils.LogSource.TEST,
                "Upsert result: " + (result ? "SUCCESS" : "FAILURE") +
                        " Performed in: " + timer.elapsed(TimeUnit.MILLISECONDS) + "ms"
        );

        this.retrieve();

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

    private TestObject getFilter() {
        Map<TestObject.TestEnum, String> testValues = this.getTestValues();

        // TEST1 stands for the identifier

        return new TestObject(
                testValues.get(
                        TestObject.TestEnum.TEST1
                )
        );
    }

    private void retrieve() {

        Map<TestObject.TestEnum, String> testValues = this.getTestValues();

        // TEST1 stands for the identifier
        TestObject filter = this.getFilter();

        LogUtils.info(
                LogUtils.LogSource.TEST,
                "Finding object with identifier: " + filter
                        .getValues()
                        .get(TestObject.TestEnum.TEST1)
        );

        TestObject result = db.findOne(filter); //.join();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(
                testValues.get(TestObject.TestEnum.TEST2),
                result.getValues().get(TestObject.TestEnum.TEST2)
        );

        LogUtils.info(
                LogUtils.LogSource.TEST,
                "Found object: " + result
                        .getValues()
                        .toString()
        );
    }


}
