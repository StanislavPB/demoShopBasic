package org.demoshop.controllers.api;

import org.demoshop.security.dto.AuthRequest;
import org.demoshop.security.dto.AuthResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/auth")
public interface AuthApi {

    @PostMapping
    ResponseEntity<AuthResponse> authentication(@RequestBody AuthRequest request);
}
