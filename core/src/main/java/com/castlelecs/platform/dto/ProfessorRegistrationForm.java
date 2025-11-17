package com.castlelecs.platform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProfessorRegistrationForm {
    private String email;
    private String firstName;
    private String lastName;
}
