package com.mtvu.messagingservice.handler;

import com.mtvu.messagingservice.client.GroupManagementClient;
import com.mtvu.messagingservice.config.GroupManagementTestResourceLifecycleManager;
import com.mtvu.messagingservice.config.KafkaTestResourceLifecycleManager;
import com.mtvu.messagingservice.domain.message.ChatMessage;
import com.mtvu.messagingservice.domain.message.MessageType;
import com.mtvu.messagingservice.domain.message.TextMessageContent;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.reactive.messaging.kafka.Record;
import io.smallrye.reactive.messaging.providers.connectors.InMemoryConnector;
import io.smallrye.reactive.messaging.providers.connectors.InMemorySink;
import io.smallrye.reactive.messaging.providers.connectors.InMemorySource;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.ClientWebApplicationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.enterprise.inject.Any;
import javax.inject.Inject;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.awaitility.Awaitility.await;


@QuarkusTest
@QuarkusTestResource(KafkaTestResourceLifecycleManager.class)
@QuarkusTestResource(GroupManagementTestResourceLifecycleManager.class)
class MessageHandlerTest {

    @RestClient
    GroupManagementClient client;

    @Inject
    @Any
    InMemoryConnector connector;


    @Test
    public void testGroupManagementClient() {
        var group = client.getGroupById("direct:abc");
        Assertions.assertEquals("direct:abc", group.groupId());

        // Currently, it seems to be an issue with Rest Client exception customization
        // See: https://github.com/quarkusio/quarkus/issues/24185
        // Therefore, we use the generic ClientWebApplicationException for now
        Assertions.assertThrows(ClientWebApplicationException.class, () -> client.getGroupById("direct:def"));
    }

    @Test
    public void whenReceivingAChatMessageThenForwardToAllParticipants() throws InterruptedException {
        InMemorySource<Record<String, ChatMessage>> messagingTopicIn = connector.source("messaging-topic-in");
        var message = ChatMessage.builder()
                .messageType(MessageType.COUPLE)
                .attachments(new ArrayList<>())
                .date(OffsetDateTime.now())
                .replyTo(null)
                .sender("alice")
                .groupId("direct:abc")
                .content(new TextMessageContent("Bla"))
                .build();
        messagingTopicIn.send(Record.of("alice", message));

        InMemorySink<Record<String, ChatMessage>> sink = connector.sink("messaging-topic-out");

        await().<List<? extends Message<Record<String, ChatMessage>>>>until(sink::received, t -> t.size() >= 1);
        var messageReceived = sink.received().stream().map(Message::getPayload).toList();
        Assertions.assertNotNull(messageReceived);
        var receivers = messageReceived.stream().map(Record::key).toList();
        Assertions.assertTrue(receivers.contains("alice"));
        Assertions.assertTrue(receivers.contains("bob"));
    }

    @Test
    public void whenReceivingANumberOfMessagesThenTheForwardedShouldMaintainTheSameOrder() {
        InMemorySource<Record<String, ChatMessage>> messagingTopicIn = connector.source("messaging-topic-in");

        for (var i = 0; i < 100; i++) {
            var message = ChatMessage.builder()
                    .messageType(MessageType.COUPLE)
                    .attachments(new ArrayList<>())
                    .date(OffsetDateTime.now())
                    .replyTo(null)
                    .sender("alice")
                    .groupId("direct:abc")
                    .content(new TextMessageContent(String.valueOf(i)))
                    .build();
            messagingTopicIn.send(Record.of("alice", message));
        }

        InMemorySink<Record<String, ChatMessage>> sink = connector.sink("messaging-topic-out");

        await().<List<? extends Message<Record<String, ChatMessage>>>>until(sink::received, t -> t.size() >= 200);
        var messageReceived = sink.received().stream().map(Message::getPayload).toList();
        Assertions.assertNotNull(messageReceived);

        var aliceMessages = messageReceived.stream().filter(x -> x.key().equals("alice")).map(Record::value).toList();
        for (var i = 0; i < 100; i++) {
            var currentMessage = aliceMessages.get(i);
            TextMessageContent messageContent = (TextMessageContent) currentMessage.getContent();
            Assertions.assertEquals(String.valueOf(i), messageContent.getContent());
        }
    }
}