package it.mikeslab.commons.api.database.impl;

import it.mikeslab.commons.api.database.helper.DatabaseTestHelper;
import it.mikeslab.commons.api.database.util.URIUtil;
import it.mikeslab.commons.api.logger.LogUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DatabaseTest {

    /**
     * Establishes a connection with each Database Implementation and then
     * perform a set of CRUD operations.
     */
    @Test
    public void test() {

        System.out.println("DANGEROUS TEST CODE: " + System.getenv("MONGO_TEST_DB_TABLE"));

        for(Database database : Database.values()) {

            LogUtils.info(
                    LogUtils.LogSource.TEST,
                    "Now running tests for " + database.name()
            );

            this.runTest(database);
        }

    }

    /**
     * Run tests for the target database
     * @param database the target database
     */
    private void runTest(Database database) {

        DatabaseTestHelper testHelper = null;

        switch (database) {

            case MONGODB:
                testHelper = new DatabaseTestHelper(
                        URIUtil.getMongoTestURI()
                );
                break;

            case SQL:
                testHelper = new DatabaseTestHelper(
                        URIUtil.getSQLTestURI()
                );
                break;
            default:
                LogUtils.warn(
                        LogUtils.LogSource.TEST,
                        "This database type is not currently handled by the DatabaseTest class"
                );
                break;
        }

        this.performTest(testHelper);

    }


    /**
     * Performs CRUD operations for the target database
     * @param testHelper the database implementation wrapper
     */
    private void performTest(DatabaseTestHelper testHelper) {

        if(testHelper == null) return;

        testHelper.setUp();

        Assertions.assertTrue(testHelper.isConnectionValid());

        testHelper.deleteIfExists();
        testHelper.testUpsert();
        testHelper.tearDown();

    }

    /**
     * Currently handled database types
     */
    private enum Database {
        MONGODB,
        SQL
    }

}
