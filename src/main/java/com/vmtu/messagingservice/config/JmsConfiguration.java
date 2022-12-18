package com.vmtu.messagingservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsOperations;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import javax.jms.ConnectionFactory;
import javax.jms.Session;

/**
 * @author mvu
 * @project chat-socket
 **/
@Configuration
@EnableJms
public class JmsConfiguration {

    @Value("${jms.client-id}")
    String clientId;

    @Bean
    public MessageConverter jacksonJmsMsgConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        return converter;
    }

    @Bean
    public JmsOperations jmsOperations(CachingConnectionFactory cachingConnectionFactory) {
        JmsTemplate jmsTemplate = new JmsTemplate(cachingConnectionFactory);
        jmsTemplate.setPubSubDomain(true);
        jmsTemplate.setReceiveTimeout(2000);
        jmsTemplate.setMessageConverter(jacksonJmsMsgConverter());
        return jmsTemplate;
    }

    @Bean
    public JmsListenerContainerFactory<?> topicListenerFactory(
        ConnectionFactory connectionFactory, DefaultJmsListenerContainerFactoryConfigurer configurer) {

        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setPubSubDomain(true);  // must follow the configurer.configure(),
                                        // because it will set the factory as default as connectionFactory
        factory.setConcurrency("1");
        factory.setSubscriptionShared(true);
        factory.setSubscriptionDurable(true);
        factory.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
        factory.setClientId(clientId);
        factory.setMessageConverter(jacksonJmsMsgConverter());
        return factory;
    }
}
