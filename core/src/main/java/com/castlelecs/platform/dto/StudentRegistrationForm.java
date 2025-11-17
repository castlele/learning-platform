package com.castlelecs.platform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudentRegistrationForm {
    private String email;
    private String firstName;
    private String lastName;
}
