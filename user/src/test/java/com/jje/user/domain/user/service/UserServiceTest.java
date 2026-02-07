package com.jje.user.domain.user.service;

import com.jje.user.common.exception.DuplicateEmailException;
import com.jje.user.common.exception.EntityNotFoundException;
import com.jje.user.domain.user.dto.UserCreateRequest;
import com.jje.user.domain.user.dto.UserResponse;
import com.jje.user.domain.user.entity.User;
import com.jje.user.domain.user.entity.UserStatus;
import com.jje.user.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("사용자를 정상적으로 등록한다")
    void createUser() {
        // given
        User user = User.builder()
                .email("test@example.com")
                .name("홍길동")
                .phone("010-1234-5678")
                .build();

        given(userRepository.existsByEmail("test@example.com")).willReturn(false);
        given(userRepository.save(any(User.class))).willReturn(user);

        UserCreateRequest request = createRequest("test@example.com", "홍길동", "010-1234-5678");

        // when
        UserResponse response = userService.create(request);

        // then
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        assertThat(response.getName()).isEqualTo("홍길동");
        assertThat(response.getPhone()).isEqualTo("010-1234-5678");
        assertThat(response.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    @DisplayName("중복 이메일로 등록 시 예외가 발생한다")
    void createUserDuplicateEmail() {
        // given
        given(userRepository.existsByEmail("test@example.com")).willReturn(true);

        UserCreateRequest request = createRequest("test@example.com", "홍길동", "010-1234-5678");

        // when & then
        assertThatThrownBy(() -> userService.create(request))
                .isInstanceOf(DuplicateEmailException.class);
    }

    @Test
    @DisplayName("존재하지 않는 사용자 조회 시 예외가 발생한다")
    void getUserNotFound() {
        // given
        given(userRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.getById(999L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("사용자를 비활성화(탈퇴)한다")
    void deactivateUser() {
        // given
        User user = User.builder()
                .email("test@example.com")
                .name("홍길동")
                .phone("010-1234-5678")
                .build();

        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        // when
        userService.deactivate(1L);

        // then
        assertThat(user.getStatus()).isEqualTo(UserStatus.WITHDRAWN);
    }

    private UserCreateRequest createRequest(String email, String name, String phone) {
        try {
            UserCreateRequest request = new UserCreateRequest();
            var emailField = UserCreateRequest.class.getDeclaredField("email");
            emailField.setAccessible(true);
            emailField.set(request, email);

            var nameField = UserCreateRequest.class.getDeclaredField("name");
            nameField.setAccessible(true);
            nameField.set(request, name);

            var phoneField = UserCreateRequest.class.getDeclaredField("phone");
            phoneField.setAccessible(true);
            phoneField.set(request, phone);

            return request;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
