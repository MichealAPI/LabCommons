package it.mikeslab.widencommons.api.database.pojo;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class RetrievedEntry {

    private final int id;
    private final Object object;

}
