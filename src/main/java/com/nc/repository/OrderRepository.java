package com.nc.repository;

import com.nc.enums.Status;
import com.nc.model.Hardware;
import com.nc.model.Order;
import com.nc.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser_Id(long id);

    Order findById(long id);

    List<Order> findByUser(User user);

    List<Order> findByUserAndStatus(User user, Status status);

    Order findByHardwareAndUserAndStatus(Hardware hardware, User user, Status status);

    void deleteAllByUserId(long id);
}
