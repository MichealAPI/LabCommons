package it.mikeslab.widencommons.api.database.pojo;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@Deprecated(forRemoval = true)
public class RetrievedEntry {

    private final Object object;
    private final int id;

}
