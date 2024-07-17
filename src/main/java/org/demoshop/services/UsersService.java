package org.demoshop.services;

import lombok.RequiredArgsConstructor;
import org.demoshop.dto.NewUserDto;
import org.demoshop.dto.UserDto;
import org.demoshop.exceptions.RestException;
import org.demoshop.models.ConfirmationCode;
import org.demoshop.models.User;
import org.demoshop.repositories.ConfirmationCodesRepository;
import org.demoshop.repositories.UsersRepository;
import org.demoshop.services.mail.MailCreateUtil;
import org.demoshop.services.mail.UserMailSender;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UsersService {

    private final UsersRepository usersRepository;
    private final ConfirmationCodesRepository confirmationCodesRepository;
    // добавили 16.07
    private final UserMailSender userMailSender;
    private final MailCreateUtil mailCreateUtil;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserDto register(NewUserDto newUser) {
        if (usersRepository.existsByEmail(newUser.getEmail())) {
            throw new RestException(HttpStatus.CONFLICT, "Пользователь с таким email уже существует: " + newUser.getEmail());
        }

        User user = User.builder()
                .email(newUser.getEmail())
                //.hashPassword(newUser.getPassword()) // Тут должно быть хеширование пароля
                .hashPassword(passwordEncoder.encode(newUser.getPassword()))
                .role(User.Role.USER)
                .firstName(newUser.getFirstName())
                .lastName(newUser.getLastName())
                .state(User.State.NOT_CONFIRMED)
                .build();
        usersRepository.save(user);

        String codeValue = UUID.randomUUID().toString();
        // тут будет отправка на почту
        /*
        	123e4567-e89b-12d3-a456-426614174000
         */
        saveConfirmCode(user, codeValue);
        // Отправка кода по электронной почте (метод sendEmail)
        sendEmail(user, codeValue);
        return UserDto.from(user);
    }

    private void saveConfirmCode(User user, String codeValue) {
        ConfirmationCode code = ConfirmationCode.builder()
                .code(codeValue)
                .user(user)
                .expiredDateTime(LocalDateTime.now().plusDays(1)) // Увеличиваем время действия кода
                .build();
        confirmationCodesRepository.save(code);
    }

    @Transactional
    public boolean confirm(String confirmCode) {
        ConfirmationCode code = confirmationCodesRepository
                .findByCodeAndExpiredDateTimeAfter(confirmCode, LocalDateTime.now())
                .orElseThrow(() -> new RestException(HttpStatus.NOT_FOUND, "Код не найден или срок его действия истек"));

        User user = code.getUser();
        user.setState(User.State.CONFIRMED);
        usersRepository.save(user);

        return true;
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

    private void sendEmail(User user, String code) {
        String link =  code;
        String html = mailCreateUtil.createConfirmationMail(user.getFirstName(), user.getLastName(), link);
        userMailSender.send(user.getEmail(), "Registration", html);
    }

}
