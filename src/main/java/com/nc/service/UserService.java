package com.nc.service;


import com.nc.model.User;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.List;

public interface UserService {
    List<User> findAll();

    User findByLogin(String login);

    void save(User user);

    boolean update(User user, String confPassword, String newPassword);

    User findAuthenticationUser();

    List<User> findUsersForAdmin();

    User findById(long id);

    void delete(long id);

    boolean addNewUser(User user, BindingResult bindingResult, Model model, String urlAddress);

    public boolean activateUser(String code);
}
