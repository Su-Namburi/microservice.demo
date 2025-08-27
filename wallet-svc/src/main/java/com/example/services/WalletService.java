package com.example.services;

import com.example.models.Wallet;
import com.example.models.WalletStatus;
import com.example.repositories.WalletRepository;
import lombok.Setter;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    Logger logger = LoggerFactory.getLogger(WalletService.class);

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
        }

        wallet = Wallet.builder()
                .walletId(UUID.randomUUID().toString())
                .userId(id)
                .balance(this.initialBalance)
                .walletStatus(WalletStatus.ACTIVE)
                .build();

        this.walletRepository.save(wallet);
    }
}
