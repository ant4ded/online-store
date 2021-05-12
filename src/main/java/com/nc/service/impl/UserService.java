package com.nc.service.impl;

import com.nc.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final PersonRepository dao;

    @Override
    public UserDetails loadUserByUsername(String username) {
        return dao.findByLogin(username);
    }
}
