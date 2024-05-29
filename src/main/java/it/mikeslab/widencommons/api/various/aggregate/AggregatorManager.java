package it.mikeslab.widencommons.api.various.aggregate;

import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * This aggregator manager will allow multiple aggregators
 * to be managed and merged into one document
 */
public class AggregatorManager {

    private final List<Aggregator<?>> aggregators;

    /**
     * Create a new aggregator manager
     */
    public AggregatorManager() {
        this.aggregators = new ArrayList<>();
    }

    /**
     * Add an aggregator to the manager
     * @param aggregator the aggregator to add
     */
    public void addAggregator(Aggregator<?> aggregator) {
        this.aggregators.add(aggregator);
    }

    /**
     * Remove an aggregator from the manager
     * @param aggregator the aggregator to remove
     */
    public void removeAggregator(Aggregator<?> aggregator) {
        this.aggregators.remove(aggregator);
    }

    /**
     * Aggregate the data from the reference
     * @param reference the reference to aggregate
     * @return the aggregated data
     */
    public Document aggregate(Object reference) {

        Document result = new Document();

        for (Aggregator<?> aggregator : this.aggregators) {
            aggregator.aggregate(reference).thenAccept(result::putAll);
        }

        return result;
    }

}
