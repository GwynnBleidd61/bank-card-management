package com.example.bankcards;

import com.example.bankcards.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BankCardsApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankCardsApplication.class, args);
    }

    @Bean
    public CommandLineRunner initAdmin(UserService userService) {
        return args -> userService.ensureAdminUser();
    }
}
