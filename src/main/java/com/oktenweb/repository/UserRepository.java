package com.oktenweb.repository;

import com.oktenweb.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, Integer> {


    User findByEmail(String email);


}
