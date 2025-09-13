package com.example;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener {

    Logger logger = LoggerFactory.getLogger(NotificationApplication.class);

    @KafkaListener(topics = "wallet-notifs",groupId = "notification-updates")
    public void getWalletNotifs(String message) {

        JSONObject jsonObject = (JSONObject) JSONValue.parse(message);
        String msg = jsonObject.get("message").toString();
        logger.info("{}",msg);
    }
    @KafkaListener(topics = "txn-notifs",groupId = "notification-updates")
    public void getTxnNotifs(String message) {

//        JSONObject jsonObject = (JSONObject) JSONValue.parse(message);
//        String msg = jsonObject.get("message").toString();
        logger.info("{}",message);
    }
}
