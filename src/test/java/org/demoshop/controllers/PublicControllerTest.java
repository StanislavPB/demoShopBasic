package org.demoshop.controllers;

import org.demoshop.models.ConfirmationCode;
import org.demoshop.models.User;
import org.demoshop.repositories.ConfirmationCodesRepository;
import org.demoshop.repositories.UsersRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
class PublicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ConfirmationCodesRepository confirmationCodesRepository;

    @Autowired
    private UsersRepository usersRepository;

    @BeforeEach
    void setUp() {
        User testUser = new User();
        testUser.setFirstName("user1");
        testUser.setLastName("user1");
        testUser.setEmail("user1@gmail.com");
        testUser.setHashPassword("Qwerty007!");
        testUser.setRole(User.Role.USER);
        testUser.setState(User.State.NOT_CONFIRMED);
        User savedUser = usersRepository.save(testUser);

        ConfirmationCode code = new ConfirmationCode();
        code.setCode("someConfirmationCode");
        code.setUser(savedUser);
        code.setExpiredDateTime(LocalDateTime.now().plusDays(1));
        confirmationCodesRepository.save(code);
    }

    @AfterEach
    void drop() {
        confirmationCodesRepository.deleteAll();
        usersRepository.deleteAll();
    }


    @Test
    void testRegisterUser() throws Exception {
        String newUserJson = """
                {
                    "firstName": "John",
                    "lastName": "User",
                    "email": "john@gmail.com",
                    "password":"Password123!"
                    }
                """;
        mockMvc.perform(post("/api/public/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUserJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("john@gmail.com"));

    }



    @Test
    public void return400ForBadFormatEmail() throws Exception {

        String newUserJson = """
                {
                    "firstName": "John",
                    "lastName": "User",
                    "email": "badFormatEmail",
                    "password":"Password123!"
                    }
                """;
        mockMvc.perform(post("/api/public/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUserJson))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void return409ForExistEmail() throws Exception {

        String newUserJson = """
                {
                    "firstName": "user1",
                    "lastName": "user1",
                    "email": "user1@gmail.com",
                    "password":"Password123!"
                    }
                """;
        mockMvc.perform(post("/api/public/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUserJson))
                .andExpect(status().isConflict());

    }

    @Test
    public void testRegisterForExistEmail() throws Exception {
        String newUserJson = """
                {
                    "firstName": "user1",
                    "lastName": "user1",
                    "email": "user1@gmail.com",
                    "password":"Password123!"
                    }
                """;

        mockMvc.perform(post("/api/public/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUserJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Exception Type: RestException"));

    }

}
