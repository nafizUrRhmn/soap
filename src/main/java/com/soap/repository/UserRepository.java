package com.soap.repository;

import com.soap.entity.User;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

    public User findUserByUsername(String username){
        User user = new User();
        user.setUsername(username);
        user.setPassword("123456");
        user.setFirstName("Nafiz");
        user.setLastName("Rahman");
        return user;
    }
}
