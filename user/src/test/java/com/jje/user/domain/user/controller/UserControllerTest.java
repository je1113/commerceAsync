package com.jje.user.domain.user.controller;

import tools.jackson.databind.ObjectMapper;
import com.jje.user.common.exception.EntityNotFoundException;
import com.jje.user.domain.user.dto.UserResponse;
import com.jje.user.domain.user.entity.UserStatus;
import com.jje.user.domain.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Test
    @DisplayName("POST /api/users - 사용자 등록 성공 201")
    void createUser() throws Exception {
        // given
        UserResponse response = UserResponse.builder()
                .id(1L)
                .email("test@example.com")
                .name("홍길동")
                .phone("010-1234-5678")
                .status(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        given(userService.create(any())).willReturn(response);

        String requestBody = """
                {
                    "email": "test@example.com",
                    "name": "홍길동",
                    "phone": "010-1234-5678"
                }
                """;

        // when & then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("GET /api/users/{id} - 존재하지 않는 사용자 조회 시 404")
    void getUserNotFound() throws Exception {
        given(userService.getById(999L))
                .willThrow(new EntityNotFoundException("사용자", 999L));

        mockMvc.perform(get("/api/users/999"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("사용자을(를) 찾을 수 없습니다. id=999"));
    }

    @Test
    @DisplayName("POST /api/users - 잘못된 이메일 형식 시 400")
    void createUserWithInvalidEmail() throws Exception {
        String requestBody = """
                {
                    "email": "invalid-email",
                    "name": "홍길동",
                    "phone": "010-1234-5678"
                }
                """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("email"));
    }
}
