package it.mikeslab.widencommons.api.database.config;

import it.mikeslab.widencommons.api.database.Database;
import it.mikeslab.widencommons.api.database.SerializableMapConvertible;
import it.mikeslab.widencommons.api.database.SupportedDatabase;
import it.mikeslab.widencommons.api.database.impl.MongoDatabaseImpl;
import it.mikeslab.widencommons.api.database.impl.SQLDatabaseImpl;
import it.mikeslab.widencommons.api.database.pojo.URIBuilder;
import it.mikeslab.widencommons.api.logger.LoggerUtil;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.logging.Level;

@RequiredArgsConstructor
public class ConfigDatabaseUtil<T extends SerializableMapConvertible<T>> {

    private final ConfigurationSection section;


    public Database<T> getDatabaseInstance() {

        String typeAsString = section.getString("type");

        SupportedDatabase dbType = validateDatabaseType(typeAsString);

        if(dbType == null) {
            LoggerUtil.log(
                    Level.WARNING,
                    LoggerUtil.LogSource.CONFIG,
                    String.format("Invalid database type: %s", typeAsString)
            );
            return null;
        }

        URIBuilder uriBuilder = composeUriBuilder(section);
        if(uriBuilder == null) {
            LoggerUtil.log(
                    Level.SEVERE,
                    LoggerUtil.LogSource.CONFIG,
                    "An error occurred. Check for previous error messages in console."
            );

            return null;
        }


        Database<T> databaseInstance = null;
        switch (dbType) {

            case SQL -> databaseInstance = new SQLDatabaseImpl<>(uriBuilder);
            case MONGODB -> databaseInstance = new MongoDatabaseImpl<>(uriBuilder);

        }

        return databaseInstance;

    }


    private URIBuilder composeUriBuilder(ConfigurationSection section) {

        URIBuilder theUriBuilder;
        URIBuilder.URIBuilderBuilder uriBuilderBuilder = URIBuilder.builder();

        String uri = section.getString("uri", null);
        if(uri == null) {
            LoggerUtil.log(Level.SEVERE, LoggerUtil.LogSource.CONFIG, "Database URI is null! Check your config!");
            return null;
        }

        uriBuilderBuilder.uri(uri);

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
