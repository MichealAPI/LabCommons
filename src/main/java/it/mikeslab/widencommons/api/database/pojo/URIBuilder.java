package it.mikeslab.widencommons.api.database.pojo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class URIBuilder {

    private String
            uri,
            username,
            password,
            database,
            table;

    private boolean isSqlite;

}
