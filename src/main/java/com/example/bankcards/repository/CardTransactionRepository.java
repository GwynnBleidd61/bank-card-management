package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardTransactionRepository extends JpaRepository<CardTransaction, Long> {

    List<CardTransaction> findByFromCardOrToCard(Card fromCard, Card toCard);

    boolean existsByFromCard(Card fromCard);

    boolean existsByToCard(Card toCard);
}
