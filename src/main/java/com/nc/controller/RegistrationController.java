package com.nc.controller;

import com.nc.model.Category;
import com.nc.model.User;
import com.nc.service.impl.CategoryServiceImpl;
import com.nc.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class RegistrationController {
    private final UserServiceImpl userService;
    private final CategoryServiceImpl categoryService;

    @RequestMapping(value = "/registration", method = RequestMethod.GET)
    public String registration(Model model) {
        log.info("Go to the registration page.");
        List<Category> categories = categoryService.findAll();
        model.addAttribute("categories", categories);
        model.addAttribute("userForm", new User());
        return "registration";
    }

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public String registration(@ModelAttribute("userForm") @Valid User user, BindingResult bindingResult, Model model, HttpServletRequest request) {
        if (userService.addNewUser(user, bindingResult, model, request.getLocalAddr())) return "registration";
        log.info("New User Registration.");
        return "redirect:/login";
    }

    @GetMapping("/activate/{code}")
    public String activate(Model model, @PathVariable String code) {
        boolean isActivated = userService.activateUser(code);
        List<Category> categories = categoryService.findAll();
        model.addAttribute("categories", categories);
        if (isActivated) {
            model.addAttribute("message", "Ваша активация прошла успешно");
            log.info("New user activation.");
        } else {
            model.addAttribute("message", "Код активации не найден!");
            log.info("Activation code not found!(" + code + ")");
        }
        model.addAttribute("isActivated", isActivated);
        return "login";
    }
}
