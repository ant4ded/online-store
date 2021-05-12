package com.nc.controller;

import com.nc.enums.Status;
import com.nc.model.Category;
import com.nc.model.Order;
import com.nc.model.Person;
import com.nc.service.impl.CategoryServiceImpl;
import com.nc.service.impl.CurrencySingleton;
import com.nc.service.impl.OrderServiceImpl;
import com.nc.service.impl.PersonServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Slf4j
@Controller
@PreAuthorize("hasAnyAuthority('ADMIN','USER','MANAGER')")
@RequiredArgsConstructor
public class CartController {
    private final CategoryServiceImpl categoryService;
    private final PersonServiceImpl personService;
    private final OrderServiceImpl orderService;

    @RequestMapping(value = "/cart", method = RequestMethod.GET)
    public String cart(Model model) {
        log.info("Go to user’s cart page");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Person person = personService.findByLogin(auth.getName());
        List<Category> categories = categoryService.findAll();
        List<Order> orders = orderService.findByPersonAndStatus(person, Status.IN_CART);
        double totalPrice = orderService.createTotalCost(orders);
        model.addAttribute("currency", CurrencySingleton.getDollarCurrency());
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("categories", categories);
        model.addAttribute("orders", orders);
        return "cart";
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public String deleteOrder(@RequestParam(name = "orderId") long idOrder) {
        log.info("Delete order");
        orderService.delete(idOrder);
        return "redirect:cart";
    }

    @RequestMapping(value = "/checkout", method = RequestMethod.GET)
    public String checkoutOrders(Model model) {
        log.info("Checkout orders");
        if (!orderService.checkoutOrdersForUser()) {
            model.addAttribute("message_busy", "Один или несколько из товаров не были оформлены. " +
                    "Причина: отсутсвие товара в наличи, возможно его заказали первее Вас.");
            return "error";
        }
        return "redirect:cart";
    }
}
