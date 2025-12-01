package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardTransactionRepository extends JpaRepository<CardTransaction, Long> {

    // Все транзакции по конкретной карте (как исходящие, так и входящие)
    List<CardTransaction> findByFromCardOrToCard(Card fromCard, Card toCard);
}
