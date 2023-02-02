package com.mtvu.messagingservice.domain.group;


import java.time.OffsetDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public record ChatGroup() {
    public enum Response {;
        public record Public(String groupId, String name, String description, String avatar,
                             Set<String> participants, OffsetDateTime createdAt) {
        }
    }
}


