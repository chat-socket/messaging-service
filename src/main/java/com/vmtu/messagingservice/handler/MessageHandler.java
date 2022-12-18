package com.vmtu.messagingservice.handler;


import com.vmtu.messagingservice.client.GroupManagementClient;
import com.vmtu.messagingservice.domain.Message;
import com.vmtu.messagingservice.exception.InvalidateUserMessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * @author mvu
 * @project chat-socket
 **/
@Component
public class MessageHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageHandler.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private GroupManagementClient groupManagementClient;

    @JmsListener(destination = "${jms.messaging.topic}", containerFactory = "topicListenerFactory")
    public void handleMessage(Message message) throws Exception {
        LOGGER.info("Getting message: {}", message);
        var group = groupManagementClient.getGroup(message.getGroupId());
        if (!group.getParticipants().contains(message.getFrom())) {
            throw new InvalidateUserMessagingException(message.toString());
        }

        for (String participant : group.getParticipants()) {
            messagingTemplate.convertAndSendToUser(participant, "/topic/notifications", message);
        }
    }
}
