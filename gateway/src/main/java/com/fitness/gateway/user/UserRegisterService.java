package com.fitness.gateway.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserRegisterService {

    private final WebClient userServiceWebClient;


    public Mono<Boolean> validateUser(String userId) {

        log.info("validateUser:  "+userId);
            return userServiceWebClient.get()
                    .uri("/api/users/{userId}/validateUser", userId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .onErrorResume(WebClientResponseException.class, ex -> Mono.just(false));
        }

    public Mono<UserResponse> registerUser(UserRequest request) {
        log.info("Registration API for user:  "+request.getEmail());
        return userServiceWebClient.post()
                .uri("/api/users/register")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(UserResponse.class)
                .onErrorResume(WebClientResponseException.class, ex -> {;
                    log.error("Error during user registration: " + ex.getMessage());
                    return Mono.error(new RuntimeException("User registration failed", ex));
                });
    }
}
