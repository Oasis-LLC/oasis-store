package com.oasis.onlinestore.repository;

import com.oasis.onlinestore.domain.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ItemRepository extends JpaRepository<Item, UUID> {

    Page<Item> findAll(Pageable pageable);

    Optional<Item> findById(UUID uuid);

    List<Item> findByNameContaining(String keyword);
}
