package com.example.services;

import com.example.models.Transaction;
import com.example.models.TransactionStatus;
import com.example.repositories.TransactionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public String initiate(Long sender, Long receiver, Long amount, String remarks) {

        Transaction transaction = Transaction.builder()
                .externalId(UUID.randomUUID().toString())
                .sender(sender)
                .receiver(receiver)
                .amount(amount)
                .remarks(remarks)
                .status(TransactionStatus.PENDING)
                .build();

        this.transactionRepository.save(transaction);

        JSONObject jsonObject = this.objectMapper.convertValue(transaction, JSONObject.class);

        this.kafkaTemplate.send("initiate-txn",jsonObject.toString());

        return transaction.getExternalId();
    }
}
