package com.vmtu.messagingservice.domain;

import java.util.Set;

/**
 * @author mvu
 * @project chat-socket
 **/
public class Group {
    private String groupId;
    private Set<String> participants;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Set<String> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<String> participants) {
        this.participants = participants;
    }
}
