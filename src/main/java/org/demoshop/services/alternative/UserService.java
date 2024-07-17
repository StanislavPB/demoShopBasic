package org.demoshop.services.alternative;

import lombok.RequiredArgsConstructor;
import org.demoshop.dto.NewUserDto;
import org.demoshop.dto.UserDto;
import org.demoshop.exceptions.RestException;
import org.demoshop.models.User;
import org.demoshop.repositories.UsersRepository;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor

public class UserService {

    private final UsersRepository usersRepository;
    private final ConfirmationCodeService confirmationCodeService;
    private final MailService mailService;

    @Transactional
    public UserDto register(NewUserDto newUser) {
        if (usersRepository.existsByEmail(newUser.getEmail())) {
            throw new RestException(HttpStatus.CONFLICT, "Пользователь с таким email уже существует: " + newUser.getEmail());
        }

        User user = User.builder()
                .email(newUser.getEmail())
                .hashPassword(newUser.getPassword()) // Тут должно быть хеширование пароля
                .role(User.Role.USER)
                .firstName(newUser.getFirstName())
                .lastName(newUser.getLastName())
                .state(User.State.NOT_CONFIRMED)
                .build();
        usersRepository.save(user);

        String codeValue = confirmationCodeService.createAndSaveCode(user);
        mailService.sendConfirmationEmail(user, codeValue);

        return UserDto.from(user);
    }

    public List<UserDto> findAll() {
        return UserDto.from(usersRepository.findAll());
    }

    public UserDto getUserById(Long userId) {
        User user = usersRepository.findById(userId)
                .orElseThrow(() -> new RestException(HttpStatus.NOT_FOUND, "Пользователь с ID " + userId + " не найден"));
        return UserDto.from(user);
    }

    public UserDto editUser(Long userId, UserDto userDto) {
        User user = usersRepository.findById(userId)
                .orElseThrow(() -> new RestException(HttpStatus.NOT_FOUND, "Пользователь с ID " + userId + " не найден"));
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setRole(User.Role.valueOf(userDto.getRole()));
        usersRepository.save(user);
        return UserDto.from(user);
    }

    public void deleteUser(Long userId) {
        if (!usersRepository.existsById(userId)) {
            throw new RestException(HttpStatus.NOT_FOUND, "Пользователь с ID " + userId + " не найден");
        }
        usersRepository.deleteById(userId);
    }
}
