package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.bankcards.entity.User;



import java.util.List;

public interface CardTransactionRepository extends JpaRepository<CardTransaction, Long> {

    boolean existsByFromCard(Card fromCard);

    boolean existsByToCard(Card toCard);

    @Query("""
            select t from CardTransaction t
            where t.fromCard.user = :user or t.toCard.user = :user
            """)
    Page<CardTransaction> findByUser(User user, Pageable pageable);
}
