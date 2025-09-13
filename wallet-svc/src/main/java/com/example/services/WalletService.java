package com.example.services;

import com.example.config.KafkaConfig;
import com.example.models.Wallet;
import com.example.models.WalletStatus;
import com.example.repositories.WalletRepository;
import lombok.Setter;
import org.apache.kafka.common.protocol.types.Field;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    Logger logger = LoggerFactory.getLogger(WalletService.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value("${wallet.initial.balance}")
    private Long initialBalance;

    //cannot listen until we know the group.
    //In a single consumer group more than one consumer cannot read from same topic
    // if they are from different consumer groups they can read from same topic
    @KafkaListener(topics = "user-created-topic",groupId = "walletCreationGroup")
    public void createWallet(String message) {

        JSONObject jsonObject = (JSONObject) JSONValue.parse(message);

        Long id = (Long) jsonObject.get("userId");

        Wallet wallet = this.walletRepository.findByUserId(id);

        if(wallet != null) {
            logger.info("Wallet already exists");

            JSONObject obj = new JSONObject();
            obj.put("message","Wallet already exists");
            kafkaTemplate.send("wallet-notifs",obj.toString());
        }

        wallet = Wallet.builder()
                .walletId(UUID.randomUUID().toString())
                .userId(id)
                .balance(this.initialBalance)
                .walletStatus(WalletStatus.ACTIVE)
                .build();

        this.walletRepository.save(wallet);
        JSONObject obj = new JSONObject();
        obj.put("message","Hurrah! Wallet created");
        kafkaTemplate.send("wallet-notifs",obj.toString());
    }

    @KafkaListener(topics = "initiate-txn", groupId = "updatetxn")
    public void updateWallet(String message) {

        JSONObject jsonObject = (JSONObject) JSONValue.parse(message);
        Long sender = (Long) jsonObject.get("sender");
        Long receiver = (Long) jsonObject.get("receiver");
        Long amount = (Long) jsonObject.get("amount");

        String externalId = jsonObject.get("externalId").toString();

        Wallet senderWallet = this.walletRepository.findByUserId(sender);
        Wallet receiverWallet = this.walletRepository.findByUserId(receiver);

        if(senderWallet == null || receiverWallet == null || senderWallet.getBalance() < amount) {
            this.logger.info("Transaction not possible");
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("sender",sender);
            jsonObj.put("receiver",receiver);
            jsonObj.put("amount",amount);
            jsonObj.put("externalId",externalId);
            jsonObj.put("status","FAILED");

            this.kafkaTemplate.send("updating-txn",jsonObj.toString());

            return;
        }
        senderWallet.setBalance(senderWallet.getBalance() - amount);
        receiverWallet.setBalance(receiverWallet.getBalance() + amount);
        this.walletRepository.saveAll(List.of(senderWallet,receiverWallet));

        JSONObject obj = new JSONObject();
        obj.put("sender",sender);
        obj.put("receiver",receiver);
        obj.put("amount",amount);
        obj.put("externalId",externalId);
        obj.put("status","SUCCESS");

        this.kafkaTemplate.send("updating-txn",obj.toString());

    }

}
