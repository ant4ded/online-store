package com.nc.controller;

import com.nc.model.*;
import com.nc.service.impl.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
@RequiredArgsConstructor
public class AdminController {
    private final CategoryServiceImpl categoryService;
    private final HardwareServiceImpl hardwareService;
    private final UserServiceImpl userService;
    private final OrderServiceImpl orderService;
    private final CharacteristicServiceImpl characteristicService;

    // TODO: 15.05.2021 many to many relationship category-characteristic
    // TODO: 15.05.2021 data design pattern entity-value
    // TODO: 15.05.2021 search
    // TODO: 12.05.2021 CHECK db names

    @RequestMapping("/admin")
    public String admin(Model model) {
        log.info("Go to admin panel.");
        List<Category> categories = categoryService.findAll();
        List<User> people = userService.findUsersForAdmin();
        List<Hardware> hardwares = hardwareService.findAll();
        List<Order> orders = orderService.findAll();
        model.addAttribute("categories", categories);
        model.addAttribute("characts", characteristicService.findAll());
        model.addAttribute("hardwareForm", new HardwareForm());
        model.addAttribute("categoryForm", new Category());
        model.addAttribute("characteristicForm", new Characteristic());
        model.addAttribute("people", people);
        model.addAttribute("hardwares", hardwares);
        model.addAttribute("orders", orders);

        return "admin";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/update", method = RequestMethod.GET)
    public String updateUser(@RequestParam(name = "userId") long idUser, Model model) {
        log.info("Go to the user data update page");
        List<Category> categories = categoryService.findAll();

        model.addAttribute("categories", categories);
        User user = userService.findById(idUser);
        model.addAttribute("userForm", new User());
        model.addAttribute("user", user);
        return "registration";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String updateUser(@ModelAttribute("userForm") @Valid User user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = ControllerUtils.getErrors(bindingResult);
            model.mergeAttributes(errorMap);
            log.info("Errors occurred in input fields when updating user\n" + errorMap);
            model.addAttribute("user", user);
            return "registration";

        } else {
            User oldUser = userService.findById(user.getId());
            user.setPassword(oldUser.getPassword());
            user.setActive(oldUser.isActive());
            log.info("Update user.");
            userService.save(user);
            return "redirect:/admin";
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/delete-user", method = RequestMethod.POST)
    public String deleteUser(@RequestParam(name = "userId") long idUser) {
        log.info("Delete user");
        userService.delete(idUser);
        return "redirect:/admin";
    }

    @RequestMapping(value = "/edit-hardware", method = RequestMethod.GET)
    public String editHardware(@RequestParam(name = "hardwareId") long idHardware, Model model) {
        log.info("Go to the component data update page");
        List<Category> categories = categoryService.findAll();
        model.addAttribute("categories", categories);

        Hardware hardware = hardwareService.findById(idHardware);
        model.addAttribute("hardwareForm", new HardwareForm());
        model.addAttribute("characts", characteristicService.findAll());
        model.addAttribute("hardware", hardware);
        return "edit_hardware";
    }

    @RequestMapping(value = "/edit-hardware", method = RequestMethod.POST)
    public String editExistHardware(@ModelAttribute("hardwareForm") @Valid HardwareForm hardwareForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            List<Category> categories = categoryService.findAll();
            model.addAttribute("categories", categories);

            Map<String, String> errorMap = ControllerUtils.getErrors(bindingResult);
            log.info("Errors occurred in input fields when updating component\n" + errorMap);
            model.mergeAttributes(errorMap);
            model.addAttribute("hardware", hardwareForm);
            return "edit_hardware";
        } else {
            log.info("Update hardware");
            hardwareService.editHardware(hardwareForm);
            return "redirect:/admin";
        }
    }

    @RequestMapping(value = "/add-hardware", method = RequestMethod.POST)
    public String createNewHardware(@ModelAttribute("hardwareForm") @Valid HardwareForm hardwareForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            List<Category> categories = categoryService.findAll();
            model.addAttribute("categories", categories);

            Map<String, String> errorMap = ControllerUtils.getErrors(bindingResult);
            log.info("Errors occurred in input fields when adding component\n" + errorMap);
            model.mergeAttributes(errorMap);
            return "redirect:/admin";
        } else {
            log.info("Add new hardware");
            hardwareService.addNewHardware(hardwareForm);
            return "redirect:/admin";
        }
    }

    @RequestMapping(value = "/delete-hardware", method = RequestMethod.POST)
    public String deleteHardware(@RequestParam(name = "hardwareId") long idHardware) {
        log.info("Delete hardware");
        hardwareService.delete(idHardware);
        return "redirect:/admin";
    }

    @RequestMapping(value = "/add-category", method = RequestMethod.POST)
    public String createNewCategory(@ModelAttribute("categoryForm") @Valid Category category, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = ControllerUtils.getErrors(bindingResult);
            log.info("Errors occurred in input fields when adding category\n" + errorMap);
            model.mergeAttributes(errorMap);
            model.addAttribute("category", category);
            return "redirect:/admin";
        } else {
            log.info("Add new category");
            categoryService.save(category);
            return "redirect:/admin";
        }

    }

    @RequestMapping(value = "/edit-category", method = RequestMethod.GET)
    public String editCategory(@RequestParam(name = "categoryId") long idCategory, Model model) {
        List<Category> categories = categoryService.findAll();
        model.addAttribute("categories", categories);
        log.info("Go to the category data update page");
        Category category = categoryService.findById(idCategory);
        model.addAttribute("category", category);
        model.addAttribute("categoryForm", new Category());
        return "edit_charact_or_category";
    }

    @RequestMapping(value = "/edit-category", method = RequestMethod.POST)
    public String editExistCategory(@ModelAttribute("categoryForm") @Valid Category category, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            List<Category> categories = categoryService.findAll();
            model.addAttribute("categories", categories);

            Map<String, String> errorMap = ControllerUtils.getErrors(bindingResult);
            log.info("Errors occurred in input fields when updating category\n" + errorMap);
            model.mergeAttributes(errorMap);
            model.addAttribute("category", category);
            return "edit_charact_or_category";
        } else {
            log.info("Update category");
            categoryService.update(category);
            return "redirect:/admin";
        }
    }

    @RequestMapping(value = "/delete-category", method = RequestMethod.POST)
    public String deleteCategory(@RequestParam(name = "categoryId") long idCategory) {
        log.info("Delete category");
        categoryService.delete(idCategory);
        return "redirect:/admin";
    }

    @RequestMapping(value = "/add-characteristic", method = RequestMethod.POST)
    public String addNewCharacteristic(@ModelAttribute("characteristicForm") @Valid Characteristic characteristic, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = ControllerUtils.getErrors(bindingResult);
            model.mergeAttributes(errorMap);
            log.info("Errors occurred in input fields when adding characteristic\n" + errorMap);
            model.addAttribute("characteristic", characteristic);
            return "redirect:/admin";
        } else {
            log.info("Add new characteristic");
            characteristicService.save(characteristic);
            return "redirect:/admin";
        }
    }

    @RequestMapping(value = "/edit-characteristic", method = RequestMethod.GET)
    public String editCharacteristic(@RequestParam(name = "characteristicId") long idCharacteristic, Model model) {
        List<Category> categories = categoryService.findAll();
        model.addAttribute("categories", categories);
        log.info("Go to the characteristic data update page");
        Characteristic characteristic = characteristicService.findById(idCharacteristic);
        model.addAttribute("characteristic", characteristic);
        model.addAttribute("characteristicForm", new Characteristic());
        return "edit_charact_or_category";
    }

    @RequestMapping(value = "/edit-characteristic", method = RequestMethod.POST)
    public String editExistCharacteristic(@ModelAttribute("characteristicForm") @Valid Characteristic characteristic, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            List<Category> categories = categoryService.findAll();
            model.addAttribute("categories", categories);

            Map<String, String> errorMap = ControllerUtils.getErrors(bindingResult);
            log.info("Errors occurred in input fields when updating characteristic\n" + errorMap);
            model.mergeAttributes(errorMap);
            model.addAttribute("characteristic", characteristic);
            return "edit_charact_or_category";
        } else {
            log.info("Update characteristic");
            characteristicService.update(characteristic);
            return "redirect:/admin";
        }
    }

    @RequestMapping(value = "/delete-characteristic", method = RequestMethod.POST)
    public String deleteCharacteristic(@RequestParam(name = "characteristicId") long idCharacteristic) {
        Characteristic characteristic = characteristicService.findById(idCharacteristic);
        log.info("Delete characteristic");
        characteristicService.delete(characteristic);
        return "redirect:/admin";
    }

    @RequestMapping(value = "/delete-order", method = RequestMethod.POST)
    public String deleteOrderAdmin(@RequestParam(name = "orderId") long idOrder, Model model) {
        log.info("Delete order");
        orderService.delete(idOrder);
        return "redirect:/admin";
    }

    @RequestMapping(value = "/edit-order", method = RequestMethod.GET)
    public String editStatusOrder(@RequestParam(name = "orderId") long idOrder) {
        orderService.changeStatusOnDelivered(idOrder);
        log.info("Update status order");
        return "redirect:/admin";
    }
}
