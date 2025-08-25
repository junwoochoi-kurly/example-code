package com.example.demo.testcode;

import org.springframework.stereotype.Service;

@Service
public class UserService {
    
    public String findUserById(Long id) {
        return "User " + id;
    }
    
    public String createUser(String name) {
        return "Created user: " + name;
    }
}