package com.example.backend.Security.Payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {
    private String email;
    private String name;
    private String surname;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 8, message = "Password should be at least 8 characters long")
    private String password;
}
