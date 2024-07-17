package org.demoshop.controllers.api;

import org.demoshop.dto.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/api/admin")
public interface AdminApi {

    @GetMapping("/users")
    ResponseEntity<List<UserDto>> getAllUsers();

    @DeleteMapping("/users/{userId}")
    ResponseEntity<Void> deleteUser(@PathVariable Long userId);
}
