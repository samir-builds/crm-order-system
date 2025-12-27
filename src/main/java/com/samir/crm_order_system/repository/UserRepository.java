package com.samir.crm_order_system.repository;

import com.samir.crm_order_system.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
