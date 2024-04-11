package it.mikeslab.widencommons.api.database.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExamplePOJO {

    private int ciao;
    private String best;

    public ExamplePOJO(int ciao, String best) {
        this.ciao = ciao;
        this.best = best;
    }
}
