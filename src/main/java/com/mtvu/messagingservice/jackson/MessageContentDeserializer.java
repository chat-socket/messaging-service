package com.mtvu.messagingservice.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.mtvu.messagingservice.domain.message.MessageContent;
import com.mtvu.messagingservice.domain.message.MessageContentType;
import com.mtvu.messagingservice.domain.message.RecordingMessageContent;
import com.mtvu.messagingservice.domain.message.TextMessageContent;

import java.io.IOException;
import java.util.InvalidPropertiesFormatException;

public class MessageContentDeserializer extends StdDeserializer<MessageContent> {

    public MessageContentDeserializer() {
        this(MessageContent.class);
    }

    public MessageContentDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public MessageContent deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ObjectCodec codec = p.getCodec();
        JsonNode node = codec.readTree(p);
        MessageContentType contentType = MessageContentType.get(node.get("type").textValue());
        if (contentType == MessageContentType.TEXT) {
            return codec.treeToValue(node, TextMessageContent.class);
        } else if (contentType == MessageContentType.RECORDING) {
            return codec.treeToValue(node, RecordingMessageContent.class);
        }
        throw new InvalidPropertiesFormatException("Unable to decode message " + p.getValueAsString());
    }
}
