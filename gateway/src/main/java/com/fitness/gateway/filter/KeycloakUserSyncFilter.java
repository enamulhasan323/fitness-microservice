package com.fitness.gateway.filter;

import com.fitness.gateway.user.UserRequest;
import com.fitness.gateway.user.UserRegisterService;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.text.ParseException;

@Component
@Slf4j
@RequiredArgsConstructor
public class KeycloakUserSyncFilter implements WebFilter {

    private final UserRegisterService userService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        String userId = exchange.getRequest().getHeaders().getFirst("X-User-ID");
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        UserRequest registerRequest = getUserDetails(token);

        if (userId==null) {
            userId = registerRequest.getKeycloakId();
        }
        if (userId != null && token != null) {
            log.info("userId "+userId+" token "+token);
            String finalUserId = userId;
            return userService.validateUser(userId)
                    .flatMap(isValid -> {
                        if (isValid) {
                            return chain.filter(exchange);
                        } else {

                            if (registerRequest != null) {
                                return  userService.registerUser(registerRequest)
                                        .flatMap(registeredUser -> {
                                            log.info("Registered new user with ID: " + registeredUser.getId());
                                            return Mono.empty();
                                        });
                            }
                            else {
                                return Mono.error(new RuntimeException("Invalid token or user details could not be extracted."));
                            }
                        }
                    }).then(Mono.defer(()-> {
                        log.info("Completed user validation for userId: " + finalUserId);
                        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                                .header("X-User-ID", finalUserId).build();
                        return chain.filter(exchange.mutate().request(mutatedRequest).build());
                    }));
        }
        else {
            return Mono.empty();
        }
    }

    private UserRequest getUserDetails(String token) {
        try {
            String tokenWithoutBearer = token.replace("Bearer ", "").trim();
            SignedJWT signedJWT = SignedJWT.parse(tokenWithoutBearer);
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

            UserRequest userRequest = new UserRequest();
            userRequest.setEmail(claims.getStringClaim("email"));
                userRequest.setFirstName( claims.getStringClaim("given_name"));
            userRequest.setLastName(claims.getStringClaim("family_name"));
            userRequest.setKeycloakId(claims.getStringClaim("sub"));
            userRequest.setPassword("password1");
            return userRequest;

        } catch (RuntimeException | ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
