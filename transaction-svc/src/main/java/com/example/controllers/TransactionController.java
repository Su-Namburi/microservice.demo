package com.example.controllers;

import com.example.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/initiate")
    public String initiateTxn(@RequestParam Long sender, @RequestParam Long receiver,
                              @RequestParam Long amount, @RequestParam String remarks) {

        return this.transactionService.initiate(sender,receiver,amount,remarks);

    }
}
