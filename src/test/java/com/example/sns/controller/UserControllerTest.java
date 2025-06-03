package com.example.sns.controller;

import com.example.sns.controller.request.UserJoinRequest;
import com.example.sns.controller.request.UserLoginRequest;
import com.example.sns.exception.ErrorCode;
import com.example.sns.exception.SnsApplicationException;
import com.example.sns.model.User;
import com.example.sns.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Test
    public void 회원가입() throws Exception {
        String userName = "userName";
        String password = "password";

        when(userService.join(userName, password)).thenReturn(mock(User.class));

        mockMvc.perform(post("/api/v1/users/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new UserJoinRequest(userName, password)))
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void 중복_회원으로_회원가입_시_에러_반환() throws Exception {
        String userName = "userName";
        String password = "password";

        when(userService.join(userName, password)).thenThrow(new SnsApplicationException(ErrorCode.DUPLICATED_USER_NAME));

        mockMvc.perform(post("/api/v1/users/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new UserJoinRequest(userName, password)))
                ).andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    public void 로그인() throws Exception {
        String userName = "userName";
        String password = "password";

        when(userService.login(userName, password)).thenReturn("token");

        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new UserLoginRequest(userName, password)))
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void 없는_회원으로_로그인_시_에러_반환() throws Exception {
        String userName = "userName";
        String password = "password";

        when(userService.login(userName, password)).thenThrow(new SnsApplicationException(ErrorCode.USER_NOT_FOUND));

        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new UserLoginRequest(userName, password)))
                ).andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void 잘못된_비밀번호로_로그인_시_에러_반환() throws Exception {
        String userName = "userName";
        String password = "password";

        when(userService.login(userName, password)).thenThrow(new SnsApplicationException(ErrorCode.INVALID_PASSWORD));

        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new UserLoginRequest(userName, password)))
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }
}