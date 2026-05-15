package com.example.lodgingresto.service;

import com.example.lodgingresto.model.User;

public interface UserService {
    User createUser(User user);
    User findByUsername(String username);
}

