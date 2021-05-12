package com.nc.service.impl;

import com.nc.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository dao;

    @Override
    public UserDetails loadUserByUsername(String username) {
        return dao.findByLogin(username);
    }
}
