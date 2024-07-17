package org.demoshop.controllers;

import lombok.RequiredArgsConstructor;
import org.demoshop.controllers.api.UserApi;
import org.demoshop.dto.UserDto;
import org.demoshop.services.UsersService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController implements UserApi {

    private final UsersService userService;

    @Override
    public ResponseEntity<UserDto> getUserById(long userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @Override
    public ResponseEntity<UserDto> updateUser(Long userId, UserDto userDto) {
        return ResponseEntity.ok(userService.editUser(userId, userDto));
    }


    @PostMapping("/confirm")
    // /confirm?confirmationCode=...
    public ResponseEntity<String> confirmUser(@RequestParam String confirmationCode){
        if (userService.confirm(confirmationCode)) {
            return ResponseEntity.ok("User confirmed successfully");
        } else {
            return ResponseEntity.badRequest().body("Invalid confirmation code");
        }
    }
}
