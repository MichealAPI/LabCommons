package it.mikeslab.commons.api.database.config;

import it.mikeslab.commons.api.database.Database;
import it.mikeslab.commons.api.database.SerializableMapConvertible;
import it.mikeslab.commons.api.database.SupportedDatabase;
import it.mikeslab.commons.api.database.impl.JSONDatabaseImpl;
import it.mikeslab.commons.api.database.impl.MongoDatabaseImpl;
import it.mikeslab.commons.api.database.impl.SQLDatabaseImpl;
import it.mikeslab.commons.api.database.pojo.URIBuilder;
import it.mikeslab.commons.api.logger.LogUtils;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.util.logging.Level;

@RequiredArgsConstructor
public class ConfigDatabaseUtil<T extends SerializableMapConvertible<T>> {

    private final ConfigurationSection section;
    private final File dataFolder;


    public Database<T> getDatabaseInstance() {

        String typeAsString = section.getString("type");

        SupportedDatabase dbType = validateDatabaseType(typeAsString);

        if(dbType == null) {
            LogUtils.warn(
                    LogUtils.LogSource.CONFIG,
                    String.format("Invalid database type: %s", typeAsString)
            );
            return null;
        }

        URIBuilder uriBuilder = composeUriBuilder(section);
        if(uriBuilder == null) {
            LogUtils.severe(
                    LogUtils.LogSource.CONFIG,
                    "An error occurred. Check for previous error messages in console."
            );

            return null;
        }


        Database<T> databaseInstance = null;
        switch (dbType) {

            case SQL:
                databaseInstance = new SQLDatabaseImpl<>(uriBuilder);
                break;

            case MONGODB:
                databaseInstance = new MongoDatabaseImpl<>(uriBuilder);
                break;

            case JSON:
                databaseInstance = new JSONDatabaseImpl<>(uriBuilder);
                break;
        }

        return databaseInstance;

    }


    private URIBuilder composeUriBuilder(ConfigurationSection section) {

        URIBuilder theUriBuilder;
        URIBuilder.URIBuilderBuilder uriBuilderBuilder = URIBuilder.builder();

        String uri = section.getString("uri", null);
        if(uri == null) {
            LogUtils.severe(
                    LogUtils.LogSource.CONFIG,
                    "Database URI is null! Check your config!"
            );
            return null;
        }

        uri = uri.replace("{dataFolder}", dataFolder.getAbsolutePath())
                .replace("/", File.separator)
                .replace("\\", File.separator);

        uriBuilderBuilder.uri(uri);

        // check if it's sqlite
        uriBuilderBuilder.isSqlite(uri.startsWith("jdbc:sqlite"));

        String password = section.getString("password", null);
        String username = section.getString("username", null);
        String table = section.getString("table", null);
        String database = section.getString("database", null);

        if(password != null) uriBuilderBuilder.password(password);
        if(username != null) uriBuilderBuilder.username(username);
        if(database != null) uriBuilderBuilder.database(database);
        if(table != null) uriBuilderBuilder.table(table);

        theUriBuilder = uriBuilderBuilder.build();
        return theUriBuilder;
    }



    private SupportedDatabase validateDatabaseType(String typeAsString) {

        try {
            return SupportedDatabase.valueOf(typeAsString);
        } catch (Exception e) {
            return null;
        }

    }




}
