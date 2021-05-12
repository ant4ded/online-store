package com.nc.service;

import com.nc.enums.Status;
import com.nc.model.Order;
import com.nc.model.User;

import java.util.List;

public interface OrderService {
    List<Order> findAll();

    Order findById(long id);

    List<Order> findByUser_Id(long id);

    List<Order> findByUser(User user);

    void save(Order order);

    void update(Order order);

    void delete(long id);

    boolean createOneOrderForAll(long idHardware, int count);

    double createTotalCost(List<Order> orders);

    void deleteAllByUserId(long id);

    boolean checkoutOrdersForUser();

    void changeStatusOnDelivered(long id);

    List<Order> findByUserAndStatus(User user, Status status);
}
