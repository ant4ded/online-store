package com.nc.controller;

import com.nc.model.Category;
import com.nc.model.User;
import com.nc.service.impl.CategoryServiceImpl;
import com.nc.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@PreAuthorize("hasAnyAuthority('ADMIN','USER','MANAGER')")
@RequiredArgsConstructor
public class ProfileController {
    private final CategoryServiceImpl categoryService;
    private final UserServiceImpl userService;

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public String profile(Model model) {
        log.info("Go to profile page.");
        List<Category> categories = categoryService.findAll();
        User user = userService.findAuthenticationUser();
        model.addAttribute("categories", categories);
        model.addAttribute("user", user);
        model.addAttribute("userForm", new User());
        return "profile";
    }

    @RequestMapping(value = "/profile/update", method = RequestMethod.POST)
    public String updateUser(@ModelAttribute("userForm") @Valid User user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = ControllerUtils.getErrors(bindingResult);
            log.info("Errors occurred in input fields when updating user\n" + errorMap);
            model.mergeAttributes(errorMap);
            model.addAttribute("user", user);
            return "redirect:/profile";
        } else {
            log.info("Update user.");
            userService.update(user, null, null);
            return "redirect:/profile";
        }
    }

    @RequestMapping(value = "/profile/update-password", method = RequestMethod.POST)
    public String updateUser(@Param("newPassword") @Valid String newPassword,
                             @Param("confPassword") @Valid String confPassword, Model model) {
        if (newPassword != null && newPassword.length() <= 255
                && confPassword != null && confPassword.length() <= 255) {
            User user = userService.findAuthenticationUser();
            boolean isValid = userService.update(user, confPassword, newPassword);
            if (!isValid) {
                log.info("Errors occurred in input fields when updating user.");
                model.addAttribute("incorrect_password", "Не получилось изменить.");
                return "error";
            }
            log.info("Update user.");
            return "redirect:/profile";
        }
        log.info("Errors occurred in input fields when updating user");
        model.addAttribute("incorrect_password", "Не корректно введены данные для изменения пароля.");
        return "error";
    }
}
