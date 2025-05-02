package com.example.onlinestore.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserDto {
    @NotBlank(message = "Логін не може бути порожнім")
    @Size(min = 3, message = "Логін має містити принаймні 3 символи")
    private String username;

    @NotBlank(message = "Email не може бути порожнім")
    @Email(message = "Невірний формат Email")
    private String email;

    @NotBlank(message = "Пароль не може бути порожнім")
    @Size(min = 6, message = "Пароль має містити принаймні 6 символів")
    private String password;

    @NotBlank(message = "Підтвердження паролю не може бути порожнім")
    private String confirmPassword;
}
