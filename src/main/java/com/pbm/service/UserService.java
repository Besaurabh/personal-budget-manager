package com.pbm.service;

import com.pbm.entity.User;

public interface UserService {
    User save(User user);
    User findByUsername(String username);
}
