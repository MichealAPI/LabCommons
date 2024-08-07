package it.mikeslab.commons.api.inventory.util.action.internal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * All the filters that can be applied to a consumer
 */
@Getter
@RequiredArgsConstructor
public enum ConsumerFilter {

    ANY("*");

    private final String filter;

}
