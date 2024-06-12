package it.mikeslab.commons.api.chat;

import java.util.UUID;

/**
 * The ChatMessagingHandler interface is used to handle the chat messaging context
 */
public interface ChatMessagingHandler {

    /**
     * Register the chat messaging listener
     * @param referenceUUID the reference UUID
     * @param context the chat messaging context
     */
    void register(UUID referenceUUID, ChatMessagingContext context);

    /**
     * Abort the chat messaging listener
     * @param referenceUUID the reference UUID
     */
    void abort(UUID referenceUUID);


}
