package org.demoshop.services.alternative;

import lombok.RequiredArgsConstructor;
import org.demoshop.dto.UserDto;
import org.demoshop.exceptions.RestException;
import org.demoshop.models.ConfirmationCode;
import org.demoshop.models.User;
import org.demoshop.repositories.ConfirmationCodesRepository;
import org.demoshop.repositories.UsersRepository;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor

public class ConfirmationCodeService {

    private final ConfirmationCodesRepository confirmationCodesRepository;
    private final UsersRepository usersRepository;

    public String createAndSaveCode(User user) {
        String codeValue = UUID.randomUUID().toString();
        ConfirmationCode code = ConfirmationCode.builder()
                .code(codeValue)
                .user(user)
                .expiredDateTime(LocalDateTime.now().plusDays(1)) // Увеличиваем время действия кода
                .build();
        confirmationCodesRepository.save(code);
        return codeValue;
    }

    @Transactional
    public UserDto confirm(String confirmCode) {
        ConfirmationCode code = confirmationCodesRepository
                .findByCodeAndExpiredDateTimeAfter(confirmCode, LocalDateTime.now())
                .orElseThrow(() -> new RestException(HttpStatus.NOT_FOUND, "Код не найден или срок его действия истек"));

        User user = code.getUser();
        user.setState(User.State.CONFIRMED);
        usersRepository.save(user);

        return UserDto.from(user);
    }
}
