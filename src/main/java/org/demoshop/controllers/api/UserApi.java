package org.demoshop.controllers.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.demoshop.dto.StandardResponseDto;
import org.demoshop.dto.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/users")
public interface UserApi {

    @Operation(summary = "Получение информации о пользователе по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "информация о пользователе",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "404", description = "пользователь не найден",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = StandardResponseDto.class)))
    }

    )
    @GetMapping("/{userId}")
    ResponseEntity<UserDto> getUserById(@PathVariable long userId);


    @PutMapping("/{userId}")
    ResponseEntity<UserDto> updateUser(@PathVariable Long userId, @RequestBody UserDto userDto);
}
