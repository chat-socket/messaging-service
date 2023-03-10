package com.mtvu.messagingservice.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public abstract class GenericMessage {

    private String channel;

    private MessageAction messageAction;
}
