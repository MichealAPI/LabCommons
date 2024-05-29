package it.mikeslab.widencommons.api.various.aggregate;

import org.bson.Document;

import java.util.concurrent.CompletableFuture;

/**
 * This aggregator will allow multiple data sources to be aggregated into one,
 * used for the Widen Network dashboard data system.
 */
public interface Aggregator<T> {

    /**
     * Aggregate the data from the reference
     * @param reference the reference to aggregate
     * @return a {@link CompletableFuture} of the aggregated data
     */
    CompletableFuture<Document> aggregate(T reference);

}
