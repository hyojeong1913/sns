package com.example.sns.service;

import com.example.sns.exception.ErrorCode;
import com.example.sns.exception.SnsApplicationException;
import com.example.sns.fixture.UserEntityFixture;
import com.example.sns.repository.UserEntityRepository;
import com.example.sns.model.entity.UserEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockitoBean
    private UserEntityRepository userEntityRepository;

    @MockitoBean
    private BCryptPasswordEncoder encoder;

    @Test
    public void 회원가입_정상() {
        String userName = "userName";
        String password = "password";

        // mocking
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.empty());
        when(encoder.encode(password)).thenReturn("encrypt_password");
        when(userEntityRepository.save(any())).thenReturn(UserEntityFixture.get(userName, password));

        Assertions.assertDoesNotThrow(() -> userService.join(userName, password));
    }

    @Test
    public void 회원가입_중복_회원() {
        String userName = "userName";
        String password = "password";

        UserEntity fixture = UserEntityFixture.get(userName, password);

        // mocking
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(fixture));
        when(encoder.encode(password)).thenReturn("encrypt_password");
        when(userEntityRepository.save(any())).thenReturn(Optional.of(fixture));

        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> userService.join(userName, password));
        Assertions.assertEquals(ErrorCode.DUPLICATED_USER_NAME, e.getErrorCode());
    }

    @Test
    public void 로그인_정상() {
        String userName = "userName";
        String password = "password";

        UserEntity fixture = UserEntityFixture.get(userName, password);

        // mocking
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(fixture));
        when(encoder.matches(password, fixture.getPassword())).thenReturn(true);

        Assertions.assertDoesNotThrow(() -> userService.login(userName, password));
    }

    @Test
    public void 로그인_없는_회원() {
        String userName = "userName";
        String password = "password";

        // mocking
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.empty());

        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> userService.login(userName, password));
        Assertions.assertEquals(ErrorCode.USER_NOT_FOUND, e.getErrorCode());
    }

    @Test
    public void 로그인_패스워드_오류() {
        String userName = "userName";
        String password = "password";
        String wrongPassword = "wrongPassword";

        UserEntity fixture = UserEntityFixture.get(userName, password);

        // mocking
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(fixture));

        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> userService.login(userName, wrongPassword));
        Assertions.assertEquals(ErrorCode.INVALID_PASSWORD, e.getErrorCode());
    }
}
