package it.mikeslab.commons.api.database.helper;

import com.google.common.base.Stopwatch;
import it.mikeslab.commons.api.database.Database;
import it.mikeslab.commons.api.database.impl.DatabaseTest;
import it.mikeslab.commons.api.database.impl.JSONDatabaseImpl;
import it.mikeslab.commons.api.database.impl.MongoDatabaseImpl;
import it.mikeslab.commons.api.database.impl.SQLDatabaseImpl;
import it.mikeslab.commons.api.database.pojo.TestObject;
import it.mikeslab.commons.api.database.pojo.URIBuilder;
import it.mikeslab.commons.api.logger.LogUtils;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

@RequiredArgsConstructor
public class DatabaseTestHelper {

    private final URIBuilder uriBuilder;
    private final DatabaseTest.Database type;
    private Database<TestObject> db;

    /**
     * Establish a connection with the target database
     */
    public void setUp() {

        switch (type) {

            case MONGODB:
                db = new MongoDatabaseImpl<>(uriBuilder);
                break;

            case SQL:
                db = new SQLDatabaseImpl<>(uriBuilder);
                break;

            case JSON:

                // deletes the file if it exists to test
                // the creation of a new file

                File file = new File(uriBuilder.getUri());

                if (file.exists()) {
                    file.delete();
                }

                db = new JSONDatabaseImpl<>(uriBuilder);
                break;
            default:
                LogUtils.warn(
                        LogUtils.LogSource.TEST,
                        "No database implementation found for " + type.name()
                );
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


    public void testUpsert(int howMany) {

        for (int i = 0; i < howMany; i++) {

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

            this.retrieve(testObject);

        }

    }


    /**
     * Helper method to get test values
     * @return Map<TestObject.TestEnum, String>
     */
    private Map<TestObject.TestEnum, String> getTestValues() {
        
        Map<TestObject.TestEnum, String> testValues = new HashMap<>();
        
        testValues.put(TestObject.TestEnum.TEST1, UUID.randomUUID() + "");
        testValues.put(TestObject.TestEnum.TEST2, UUID.randomUUID() + "");
        testValues.put(TestObject.TestEnum.TEST3, UUID.randomUUID() + "");
        
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

    private void retrieve(TestObject obj) {

        // TEST1 stands for the identifier
        TestObject filter = new TestObject(
                obj.getValues().get(
                        TestObject.TestEnum.TEST1
                )
        );

        LogUtils.info(
                LogUtils.LogSource.TEST,
                "Finding object with identifier: " + filter
                        .getValues()
                        .get(TestObject.TestEnum.TEST1)
        );

        Stopwatch timer = Stopwatch.createStarted();

        TestObject result = db.findOne(filter); //.join();

        timer.stop();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(
                obj.getValues().get(TestObject.TestEnum.TEST2),
                result.getValues().get(TestObject.TestEnum.TEST2)
        );

        LogUtils.info(
                LogUtils.LogSource.TEST,
                "Found object: " + result
                        .getValues()
                        .toString()
                        + " in " + timer.elapsed(TimeUnit.MILLISECONDS) + "ms"
        );
    }


}
