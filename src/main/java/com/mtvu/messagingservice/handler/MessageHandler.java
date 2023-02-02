package com.mtvu.messagingservice.handler;

import com.mtvu.messagingservice.domain.message.ChatMessage;
import com.mtvu.messagingservice.client.GroupManagementClient;
import io.smallrye.reactive.messaging.kafka.Record;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class MessageHandler {

    @RestClient
    GroupManagementClient groupManagementClient;

    @Inject
    @Channel("messaging-topic-out")
    Emitter<Record<String, ChatMessage>> messageEmitter;

    @Incoming("messaging-topic-in")
    public void processMessage(Record<String, ChatMessage> message) {
        var chatGroup = groupManagementClient.getGroupById(message.value().getGroupId());
        if (chatGroup != null && chatGroup.participants().contains(message.key())) {
            // Todo: Save the message to database
            for (String participant : chatGroup.participants()) {
                messageEmitter.send(Record.of(participant, message.value()));
            }
        }
    }
}
