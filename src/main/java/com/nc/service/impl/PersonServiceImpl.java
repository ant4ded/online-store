package com.nc.service.impl;

import com.nc.config.SpringSecurityConfig;
import com.nc.enums.Role;
import com.nc.model.Order;
import com.nc.model.Person;
import com.nc.repository.PersonRepository;
import com.nc.service.PersonService;
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
public class PersonServiceImpl implements PersonService {
    private final PersonRepository dao;
    private final SpringSecurityConfig springSecurityConfig;
    private final MailSender mailSender;
    private OrderServiceImpl orderService;

    @Autowired
    public void setOrderService(OrderServiceImpl orderService) {
        this.orderService = orderService;
    }

    @Override
    public List<Person> findAll() {
        log.info("Taking data from the database (All users)");
        return dao.findAll();
    }

    @Override
    public Person findByLogin(String login) {
        log.info("Taking data from a database (User by a specific login)");
        return dao.findByLogin(login);
    }

    @Override
    public void save(Person person) {
        log.info("Writing user data to the database");
        dao.save(person);
    }

    @Override
    public boolean update(Person person, String confPassword, String newPassword) {
        Person oldPerson = dao.findById(person.getId());
        if (confPassword != null) {
            if (!newPassword.equals(confPassword))
                return false;
        }
        if (newPassword == null)
            person.setPassword(oldPerson.getPassword());
        else {
            PasswordEncoder passwordEncoder = springSecurityConfig.getPasswordEncoder();
            person.setPassword(passwordEncoder.encode(newPassword));
        }
        person.setActive(oldPerson.isActive());
        person.setRole(oldPerson.getRole());
        log.info("Updating user data in the database\n" + person.toString());
        dao.save(person);
        log.info("User reauthorization on the site");
        Collection<SimpleGrantedAuthority> nowAuthorities = (Collection<SimpleGrantedAuthority>) SecurityContextHolder
                .getContext().getAuthentication().getAuthorities();
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(person.getLogin(), person.getPassword(), nowAuthorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return true;
    }

    @Override
    public Person findAuthenticationPerson() {
        log.info("Finding an authorized user.");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return dao.findByLogin(auth.getName());
    }

    @Override
    public List<Person> findPersonsForAdmin() {
        List<Person> people = dao.findAll();
        people.removeIf(person -> person.getRole().equals(Role.ADMIN));
        log.info("Taking data from the database (All users except administrators).");
        return people;
    }

    @Override
    public Person findById(long id) {
        log.info("Taking data from the database (User by ID).");
        return dao.findById(id);
    }

    @Override
    public void delete(long id) {
        List<Order> orders = orderService.findByPerson_Id(id);
        orders.forEach(order -> order.setHardware(null));
        log.info("Removing user data from the database.");
        if (orders.size() != 0)
            orderService.deleteAllByPersonId(id);
        else
            dao.deleteById(id);
    }

    @Override
    public boolean addNewUser(Person person, BindingResult bindingResult, Model model, String urlAddress) {
        Person userFromDb = findByLogin(person.getLogin());
        PasswordEncoder passwordEncoder = springSecurityConfig.getPasswordEncoder();
        if (userFromDb != null) {
            model.addAttribute("exist_error", "Пользователь существует.");
            return true;
        }
        if (bindingResult.hasErrors()) {
            return true;

        } else {
            person.setPassword(passwordEncoder.encode(person.getPassword()));
            person.setRole(Role.USER);
            person.setActive(false);
            person.setActivationCode(UUID.randomUUID().toString());

            String message = String.format(
                    "Hello, %s! \n" +
                            "Welcome to localhost. Please, visit next link: http://localhost:8080/activate/%s",
                    person.getLogin(),
                    person.getActivationCode()
            );
            mailSender.send(person.getMail(), "Activation code", message);
            log.info("Writing user data to the database.\n" + person.toString());
            dao.save(person);
        }
        return false;
    }

    @Override
    public boolean activateUser(String code) {
        Person person = dao.findByActivationCode(code);

        if (person == null) {
            return false;
        }
        person.setActive(true);
        log.info("Updating user data in the database (Activation).\n" + person.toString());
        dao.save(person);

        return true;
    }
}
