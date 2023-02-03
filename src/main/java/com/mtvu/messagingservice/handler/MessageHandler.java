package com.mtvu.messagingservice.handler;

import com.mtvu.messagingservice.domain.message.ChatMessage;
import com.mtvu.messagingservice.client.GroupManagementClient;
import com.mtvu.messagingservice.exception.NotFoundException;
import io.smallrye.reactive.messaging.kafka.Record;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class MessageHandler {

    private static final Logger LOGGER = Logger.getLogger(MessageHandler.class);

    @RestClient
    GroupManagementClient groupManagementClient;

    @Inject
    @Channel("messaging-topic-out")
    Emitter<Record<String, ChatMessage>> messageEmitter;

    @Incoming("messaging-topic-in")
    public void processMessage(Record<String, ChatMessage> message) {
        try {
            var chatGroup = groupManagementClient.getGroupById(message.value().getGroupId());
            if (chatGroup.participants().contains(message.key())) {
                // Todo: Save the message to database
                for (String participant : chatGroup.participants()) {
                    messageEmitter.send(Record.of(participant, message.value()));
                }
            }
        } catch (NotFoundException ignore) {
            // It seems that this suspicious user is trying to access an invalid ground
            // We ignore him for now
        } catch (Exception e) {
            // Something went wrong
            LOGGER.debugv("Exception occurred when handling message from {}, payload: {}",
                    message.key(), message.value(), e);
        }
    }
}
