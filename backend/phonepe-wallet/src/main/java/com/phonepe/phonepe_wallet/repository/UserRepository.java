package com.phonepe.phonepe_wallet.repository;

import com.phonepe.phonepe_wallet.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByPhoneNumber(String phoneNumber);
    Optional<User> findByUpiId(String upiId);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByUpiId(String upiId);
}
