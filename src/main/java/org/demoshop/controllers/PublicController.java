package org.demoshop.controllers;

import lombok.RequiredArgsConstructor;
import org.demoshop.controllers.api.PublicApi;
import org.demoshop.dto.NewUserDto;
import org.demoshop.dto.UserDto;
import org.demoshop.services.UsersService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PublicController implements PublicApi {

    private final UsersService userService;

    @Override
    public ResponseEntity<UserDto> registerUser(NewUserDto newUserDto) {
        UserDto userDto = userService.register(newUserDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userDto);
    }


}
