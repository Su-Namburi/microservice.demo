package com.example.dtos;

import com.example.models.User;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUserRequest {

    @NotBlank
    private String name;

    @Email
    private String email;

    @NotBlank
    private String mobile;

    public User toUser() {
        User user = User.builder()
                .name(name)
                .email(email)
                .mobile(mobile)
                .build();

        return user;
    }
}
