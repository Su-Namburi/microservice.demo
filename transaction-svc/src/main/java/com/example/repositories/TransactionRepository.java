package com.example.repositories;

import com.example.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction,Long> {

    Transaction findByExternalId(String externalId);
}
