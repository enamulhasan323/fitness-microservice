package com.fitness.userService.Controller;

import com.fitness.userService.dto.UserRequest;
import com.fitness.userService.dto.UserResponse;
import com.fitness.userService.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {

    private UserService userService;

    @GetMapping ("/{userId}")
    public ResponseEntity<UserResponse> getUserProfileById(@PathVariable String userId) {

        return ResponseEntity.ok(userService.getUserProfileById(userId));
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser( @Valid  @RequestBody UserRequest userRequest) {

        return ResponseEntity.ok(userService.registerUser(userRequest));
    }

    @GetMapping ("/{userId}/validateUser")
    public ResponseEntity<Boolean> validateUser(@PathVariable String userId) {

        return ResponseEntity.ok(userService.existByUserId(userId));
    }
}
