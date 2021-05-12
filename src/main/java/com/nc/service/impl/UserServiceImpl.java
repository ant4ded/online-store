package com.nc.service.impl;

import com.nc.config.SpringSecurityConfig;
import com.nc.enums.Role;
import com.nc.model.Order;
import com.nc.model.User;
import com.nc.repository.UserRepository;
import com.nc.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository dao;
    private final SpringSecurityConfig springSecurityConfig;
    private final MailSender mailSender;
    private OrderServiceImpl orderService;

    @Autowired
    public void setOrderService(OrderServiceImpl orderService) {
        this.orderService = orderService;
    }

    @Override
    public List<User> findAll() {
        log.info("Taking data from the database (All users)");
        return dao.findAll();
    }

    @Override
    public User findByLogin(String login) {
        log.info("Taking data from a database (User by a specific login)");
        return dao.findByLogin(login);
    }

    @Override
    public void save(User user) {
        log.info("Writing user data to the database");
        dao.save(user);
    }

    @Override
    public boolean update(User user, String confPassword, String newPassword) {
        User oldUser = dao.findById(user.getId());
        if (confPassword != null) {
            if (!newPassword.equals(confPassword))
                return false;
        }
        if (newPassword == null)
            user.setPassword(oldUser.getPassword());
        else {
            PasswordEncoder passwordEncoder = springSecurityConfig.getPasswordEncoder();
            user.setPassword(passwordEncoder.encode(newPassword));
        }
        user.setActive(oldUser.isActive());
        user.setRole(oldUser.getRole());
        log.info("Updating user data in the database\n" + user.toString());
        dao.save(user);
        log.info("User reauthorization on the site");
        Collection<SimpleGrantedAuthority> nowAuthorities = (Collection<SimpleGrantedAuthority>) SecurityContextHolder
                .getContext().getAuthentication().getAuthorities();
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user.getLogin(), user.getPassword(), nowAuthorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return true;
    }

    @Override
    public User findAuthenticationUser() {
        log.info("Finding an authorized user.");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return dao.findByLogin(auth.getName());
    }

    @Override
    public List<User> findUsersForAdmin() {
        List<User> people = dao.findAll();
        people.removeIf(user -> user.getRole().equals(Role.ADMIN));
        log.info("Taking data from the database (All users except administrators).");
        return people;
    }

    @Override
    public User findById(long id) {
        log.info("Taking data from the database (User by ID).");
        return dao.findById(id);
    }

    @Override
    public void delete(long id) {
        List<Order> orders = orderService.findByUser_Id(id);
        orders.forEach(order -> order.setHardware(null));
        log.info("Removing user data from the database.");
        if (orders.size() != 0)
            orderService.deleteAllByUserId(id);
        else
            dao.deleteById(id);
    }

    @Override
    public boolean addNewUser(User user, BindingResult bindingResult, Model model, String urlAddress) {
        User userFromDb = findByLogin(user.getLogin());
        PasswordEncoder passwordEncoder = springSecurityConfig.getPasswordEncoder();
        if (userFromDb != null) {
            model.addAttribute("exist_error", "Пользователь существует.");
            return true;
        }
        if (bindingResult.hasErrors()) {
            return true;

        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRole(Role.USER);
            user.setActive(false);
            user.setActivationCode(UUID.randomUUID().toString());

            String message = String.format(
                    "Hello, %s! \n" +
                            "Welcome to localhost. Please, visit next link: http://localhost:8080/activate/%s",
                    user.getLogin(),
                    user.getActivationCode()
            );
            mailSender.send(user.getMail(), "Activation code", message);
            log.info("Writing user data to the database.\n" + user.toString());
            dao.save(user);
        }
        return false;
    }

    @Override
    public boolean activateUser(String code) {
        User user = dao.findByActivationCode(code);

        if (user == null) {
            return false;
        }
        user.setActive(true);
        log.info("Updating user data in the database (Activation).\n" + user.toString());
        dao.save(user);

        return true;
    }
}
