package com.uber.uberapi.controller;

import com.uber.uberapi.models.Account;
import com.uber.uberapi.repositories.AccountRepository;
import com.uber.uberapi.repositories.DriverRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class democontroller implements CommandLineRunner {

    @Autowired
    DriverRepository driverRepository;

    @Autowired
    AccountRepository accountRepository;
    @Override
    public void run(String... args) throws Exception {
        //Optional<Account> acc1 = accountRepository.findFirstByUsername("abc");
        driverRepository.findFirstByAccount_Username("abc");
        driverRepository.findFirstByNameAndAccount_Username("AJay","xyz");
        System.out.println("Commandline controller begins run");
    }
}
