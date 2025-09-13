package com.example.repositories;

import com.example.models.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet,String> {
    Wallet findByUserId(Long id);

    String findNameByUserId(Long id);
}
