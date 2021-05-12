package com.nc.service.impl;

import com.nc.enums.Status;
import com.nc.model.Hardware;
import com.nc.model.Order;
import com.nc.model.Person;
import com.nc.repository.OrderRepository;
import com.nc.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository dao;
    private final HardwareServiceImpl hardwareService;
    private final PersonServiceImpl personService;

    @Override
    public List<Order> findAll() {
        log.info("Taking data from the database (All orders)");
        return dao.findAll();
    }

    @Override
    public Order findById(long id) {
        log.info("Taking data from the database (Order by ID)");
        return dao.findById(id);
    }

    @Override
    public List<Order> findByPerson_Id(long id) {
        log.info("Taking data from the database (Orders by user ID)");
        return dao.findByPerson_Id(id);
    }

    @Override
    public List<Order> findByPerson(Person person) {
        log.info("Taking data from the database (Orders for the user)");
        return dao.findByPerson(person);
    }

    @Override
    public void save(Order order) {
        log.info("Writing order data to the database\n" + order.toString());
        dao.save(order);
    }

    @Override
    public void update(Order order) {
        Order oldOrder = dao.findById(order.getId());
        order.setHardware(oldOrder.getHardware());
        order.setCount(oldOrder.getCount());
        order.setPerson(oldOrder.getPerson());
        log.info("Updating order data in the database\n" + order.toString());
        dao.save(order);
    }

    @Override
    public void delete(long id) {
        Order order = dao.findById(id);
        if (order.getStatus().equals(Status.IN_PROCESSING)) {
            Hardware hardware = hardwareService.findById(order.getHardware().getId());
            hardware.setTotalCount(hardware.getTotalCount() + order.getCount());
            hardwareService.save(hardware);
        }
        log.info("Removing order data from the database\n" + order.toString());
        dao.deleteById(id);
    }

    @Override
    public boolean createOneOrderForAll(long idHardware, int count) {
        Hardware hardware = hardwareService.findById(idHardware);
        Person person = personService.findAuthenticationPerson();
        Order orderExist = dao.findByHardwareAndPersonAndStatus(hardware, person, Status.IN_CART);
        if (orderExist != null && orderExist.getCount() >= hardware.getTotalCount())
            return false;
        if (hardware.getTotalCount() == 0) {
            return false;
        } else {
            if (orderExist != null) {
                orderExist.setCount(orderExist.getCount() + count);
                log.info("Updating order data in the database\n" + orderExist.toString());
                dao.save(orderExist);
            } else {
                Order order = new Order(0, person, hardware, count, Status.IN_CART);
                log.info("Writing order data to the database\n" + order.toString());
                dao.save(order);
            }
            return true;
        }
    }

    @Override
    public double createTotalCost(List<Order> orders) {
        log.info("Finding the total amount of the order.");
        return orders.stream().mapToDouble(item -> item.getHardware().getPrice() * item.getCount()).sum();
    }

    @Override
    @Transactional
    public void deleteAllByPersonId(long id) {
        log.info("Delete orders by user ID");
        dao.deleteAllByPersonId(id);
    }

    @Override
    public boolean checkoutOrdersForUser() {
        boolean isAllValidate = true;
        Person person = personService.findAuthenticationPerson();
        List<Order> orders = dao.findByPersonAndStatus(person, Status.IN_CART);
        orders.forEach(order -> order.setStatus(Status.IN_PROCESSING));
        orders.forEach(order -> {
            if (order.getCount() <= order.getHardware().getTotalCount())
                order.getHardware()
                        .setTotalCount(order.getHardware().getTotalCount() - order.getCount());
            else
                order.setCount(0);
        });
        List<Order> deleteOrders = orders.stream().filter(order -> order.getCount() == 0).collect(Collectors.toList());
        if (deleteOrders.size() != 0) {
            dao.deleteAll(deleteOrders);
            isAllValidate = false;
        }
        orders.removeIf(order -> order.getCount() == 0);
        List<Hardware> hardwares = orders.stream().map(Order::getHardware).collect(Collectors.toList());
        hardwareService.saveAll(hardwares);
        log.info("Updating order data in the database");
        dao.saveAll(orders);
        return isAllValidate;
    }

    @Override
    public void changeStatusOnDelivered(long id) {
        Order order = dao.findById(id);
        order.setStatus(Status.DELIVERED);
        log.info("Updating order data in the database");
        dao.save(order);
    }

    @Override
    public List<Order> findByPersonAndStatus(Person person, Status status) {
        log.info("Taking data from the database (Orders for a specific user and status)");
        return dao.findByPersonAndStatus(person, status);
    }
}
