package com.vmtu.messagingservice.domain;

import java.io.Serializable;
import java.time.Instant;

/**
 * @author mvu
 * @project chat-socket
 **/
public class Message implements Serializable {
    private String from;
    private String groupId;
    private long timestamp;
    private String message;

    public Message() {
    }

    public Message(String from, String groupId, long timestamp, String message) {
        this.from = from;
        this.groupId = groupId;
        this.timestamp = timestamp;
        this.message = message;
    }

    public String getFrom() {
        return from;
    }

    public String getGroupId() {
        return groupId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "TransferMessage{" +
            "from='" + from + '\'' +
            ", groupId='" + groupId + '\'' +
            ", timestamp=" + timestamp +
            ", message='" + message + '\'' +
            '}';
    }
}
