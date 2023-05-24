package com.oasis.onlinestore.repository;

import com.oasis.onlinestore.domain.Address;
import com.oasis.onlinestore.domain.AddressType;
import com.oasis.onlinestore.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    User findByEmail(String email);

    boolean existsByEmail(String email);

    List<Address> findByAddresses(AddressType addressType);

}
