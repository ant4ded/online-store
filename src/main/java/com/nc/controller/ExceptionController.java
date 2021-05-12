package com.nc.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
public class ExceptionController implements ErrorController {
    @RequestMapping("/error")
    public ModelAndView handleError(HttpServletResponse response, Model model) {
        ModelAndView modelAndView = new ModelAndView();
        log.warn("There was an error on the page:\n" + response.getStatus());
        if (response.getStatus() == HttpStatus.BAD_REQUEST.value()) {
            modelAndView.setViewName("error");
            model.addAttribute("message", "400");
        } else if (response.getStatus() == HttpStatus.FORBIDDEN.value()) {
            modelAndView.setViewName("error");
            model.addAttribute("message", "403");
        } else if (response.getStatus() == HttpStatus.NOT_FOUND.value()) {
            modelAndView.setViewName("error");
            model.addAttribute("message", "404");
        } else if (response.getStatus() == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
            modelAndView.setViewName("error");
            model.addAttribute("message", "500");
        } else {
            modelAndView.setViewName("error");
        }

        return modelAndView;
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}
