package com.fitness.userService.serviceImpl;

import com.fitness.userService.dto.UserRequest;
import com.fitness.userService.dto.UserResponse;
import com.fitness.userService.model.User;
import com.fitness.userService.repository.UserRepository;
import com.fitness.userService.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    @Override
    public UserResponse registerUser(@Valid UserRequest userRequest) {

        if (userRepository.existsByEmail(userRequest.getEmail())) {
            UserResponse userResponse = userRepository.findByEmail(userRequest.getEmail());
            userResponse.setId(userResponse.getId());
            userResponse.setEmail(userResponse.getEmail());
            userResponse.setKeycloakId(userResponse.getKeycloakId());
            userResponse.setPassword(userResponse.getPassword());
            userResponse.setFirstName(userResponse.getFirstName());
            userResponse.setLastName(userResponse.getLastName());
            userResponse.setCreatedAt(userResponse.getCreatedAt());
            userResponse.setUpdatedAt(userResponse.getUpdatedAt());
            return  userResponse;
        }
        User user = new User();
        user.setEmail(userRequest.getEmail());
        user.setPassword(userRequest.getPassword());
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setKeycloakId(userRequest.getKeycloakId());
        User savedUser = userRepository.save(user);
        return getUserResponse(savedUser);
    }

    @Override
    public UserResponse getUserProfileById(String userId) {
        User user = userRepository.findByKeycloakId((userId));
        return getUserResponse(user);
    }

    @Override
    public Boolean existByUserId(String userId) {
        return userRepository.existsByKeycloakId(userId);
    }

    private UserResponse getUserResponse(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setEmail(user.getEmail());
        userResponse.setKeycloakId(user.getKeycloakId());
        userResponse.setPassword(user.getPassword());
        userResponse.setFirstName(user.getFirstName());
        userResponse.setLastName(user.getLastName());
        userResponse.setCreatedAt(user.getCreatedAt());
        userResponse.setUpdatedAt(user.getUpdatedAt());
        return userResponse;
    }
}
