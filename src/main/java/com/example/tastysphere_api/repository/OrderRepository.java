package com.example.tastysphere_api.repository;

import com.example.tastysphere_api.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long Id);
}