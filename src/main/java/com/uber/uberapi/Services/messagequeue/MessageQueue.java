package com.uber.uberapi.Services.messagequeue;

//Adapter for Third party message queue.
public interface MessageQueue {
    void sendMessage(String topic, MQMessage message);
    MQMessage consumeMessage(String topic);
}
