package com.fitnes.activityService.serviceImpl;

import com.fitnes.activityService.service.UserValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
@RequiredArgsConstructor
public class UserValidationServiceImpl implements UserValidationService {

    private final WebClient userServiceWebClient;


    @Override
    public Boolean validateUser(String userId) {

        try {
            return userServiceWebClient.get()
                    .uri("/api/users/{userId}/validateUser", userId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();
        } catch (WebClientResponseException e) {
           if (e.getStatusCode()== HttpStatus.NOT_FOUND) {
               throw new RuntimeException("User not found with id: " + userId);
           }
        }
        return false;
    }
    }
