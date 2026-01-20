package com.fitness.userService.service;

import com.fitness.userService.dto.UserRequest;
import com.fitness.userService.dto.UserResponse;

public interface UserService {
    UserResponse registerUser(UserRequest userRequest);
    UserResponse getUserProfileById(String userId);
}
