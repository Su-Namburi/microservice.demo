package com.example.services;

import com.example.models.Transaction;
import com.example.models.TransactionStatus;
import com.example.repositories.TransactionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TransactionService {

    Logger logger = LoggerFactory.getLogger(TransactionService.class);

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
    @KafkaListener(topics = "updating-txn",groupId = "transactionStatusGroup")
    public void updateTransaction(String message) {
        JSONObject obj = (JSONObject) JSONValue.parse(message);

        String externalId = obj.get("externalId").toString();
        String status = obj.get("status").toString();

        Transaction transaction = this.transactionRepository.findByExternalId(externalId);

        if(!transaction.getStatus().equals(TransactionStatus.PENDING)) {
            logger.info("Transaction is already terminated");
        }

        TransactionStatus txnStatus = status.equals("SUCCESS") ? TransactionStatus.SUCCESS : TransactionStatus.FAILED;

        transaction.setStatus(txnStatus);
        this.transactionRepository.save(transaction);

    }
}
