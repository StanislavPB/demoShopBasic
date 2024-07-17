package org.demoshop.controllers;

import lombok.RequiredArgsConstructor;
import org.demoshop.controllers.api.AdminApi;
import org.demoshop.dto.UserDto;
import org.demoshop.services.UsersService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AdminController implements AdminApi {

    private final UsersService userService;

    @Override
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @Override
    public ResponseEntity<Void> deleteUser(Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok().build();
    }
}
