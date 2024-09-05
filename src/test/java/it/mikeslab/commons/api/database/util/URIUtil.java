package it.mikeslab.commons.api.database.util;

import it.mikeslab.commons.api.database.pojo.URIBuilder;
import lombok.experimental.UtilityClass;

import java.io.File;

@UtilityClass
public class URIUtil {

    private final String MONGO_PREFIX = "MONGO";
    private final String SQL_PREFIX = "SQL";

    public URIBuilder getSQLTestURI() {
        return getDefaultURI(SQL_PREFIX);
    }

    public URIBuilder getMongoTestURI() {
        return getDefaultURI(MONGO_PREFIX);
    }

    public URIBuilder getJsonTestURI() {
        return URIBuilder.builder()
                .uri("src" +
                        File.separator + "test" +
                        File.separator + "resources" +
                        File.separator + "test.json"
                )
                .build();
    }

    private URIBuilder getDefaultURI(String prefix) {
        return URIBuilder.builder()
                .uri(System.getenv(prefix + "_TEST_DB_URL"))
                .username(System.getenv(prefix + "_TEST_DB_USER"))
                .password(System.getenv(prefix + "_TEST_DB_PASSWORD"))
                .table(System.getenv(prefix + "_TEST_DB_TABLE"))
                .database(System.getenv(prefix + "_TEST_DB"))
                .build();
    }


}
