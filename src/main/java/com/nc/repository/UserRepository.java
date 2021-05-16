package com.nc.repository;

import com.nc.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByLogin(String name);

    User findById(long id);

    User findByActivationCode(String code);
}
