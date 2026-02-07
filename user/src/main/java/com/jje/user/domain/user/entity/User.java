package com.jje.user.domain.user.entity;

import com.jje.user.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, length = 100)
    private String name;

    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    @Builder
    public User(String email, String name, String phone, UserStatus status) {
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.status = status != null ? status : UserStatus.ACTIVE;
    }

    public void update(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public void deactivate() {
        this.status = UserStatus.WITHDRAWN;
    }
}
