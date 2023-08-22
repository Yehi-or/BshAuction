package com.bsh.bshauction.global.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${SPRING_RABBITMQ_HOST}")
    private String rabbitmqHost;

    @Value("${SPRING_RABBITMQ_PORT}")
    private int rabbitmqPort;

    @Value("${SPRING_RABBITMQ_USERNAME}")
    private String rabbitmqUserName;

    @Value("${SPRING_RABBITMQ_PASSWORD}")
    private String rabbitmqPassword;

    @Value("${RABBITMQ_BID_QUEUE_NAME}")
    private String bidQueueName;

    @Value("${RABBITMQ_BID_EXCHANGE_NAME}")
    private String bidExchangeName;

    @Value("${RABBITMQ_BID_ROUTING_KEY}")
    private String bidRoutingKey;

    @Value("${RABBITMQ_BIDCANCEL_NAME}")
    private String bidCancelName;

    @Value("${RABBITMQ_BIDCANCEL_EXCHANGE_NAME}")
    private String bidCancelExchangeName;

    @Value("${RABBITMQ_BIDCANCEL_ROUTING_KEY}")
    private String bidCancelRoutingKey;

    @Value("${RABBITMQ_MESSAGE_QUEUE_NAME}")
    private String messageQueueName;

    @Value("${RABBITMQ_MESSAGE_EXCHANGE_NAME}")
    private String messageExchangeName;

    @Value("${RABBITMQ_MESSAGE_ROUTING_KEY}")
    private String messageRoutingKey;

    @Bean
    public Queue bidQueue() {
        return new Queue(bidQueueName);
    }

    @Bean
    public Queue mainQueue() { return new Queue("main"); }
    @Bean
    public Queue bidCancelQueue() {
        return new Queue(bidCancelName);
    }

    @Bean
    public Queue messageQueue() {
        return new Queue(messageQueueName);
    }

    @Bean
    public DirectExchange bidExchange() {
        return new DirectExchange(bidExchangeName);
    }

    @Bean
    public DirectExchange mainExchange() { return new DirectExchange("main.Exchange"); }

    @Bean
    public DirectExchange bidCancelExchange() {
        return new DirectExchange(bidCancelExchangeName);
    }

    @Bean
    public DirectExchange messageExchange() {
        return new DirectExchange(messageExchangeName);
    }

    @Bean
    public Binding bidBinding(Queue bidQueue, DirectExchange bidExchange) {
        return BindingBuilder.bind(bidQueue).to(bidExchange).with(bidRoutingKey);
    }

    @Bean
    public Binding bidCancelBinding(Queue bidCancelQueue, DirectExchange bidCancelExchange) {
        return BindingBuilder.bind(bidCancelQueue).to(bidCancelExchange).with(bidCancelRoutingKey);
    }

    @Bean
    public Binding messageBinding(Queue messageQueue, DirectExchange messageExchange) {
        return BindingBuilder.bind(messageQueue).to(messageExchange).with(messageRoutingKey);
    }

    @Bean
    public Binding mainBinding(Queue mainQueue, DirectExchange mainExchange) {
        return BindingBuilder.bind(mainQueue).to(mainExchange).with("main");
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(rabbitmqHost);
        connectionFactory.setPort(rabbitmqPort);
        connectionFactory.setUsername(rabbitmqUserName);
        connectionFactory.setPassword(rabbitmqPassword);
        return connectionFactory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public MessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
