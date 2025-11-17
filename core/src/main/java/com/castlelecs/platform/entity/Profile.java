package com.castlelecs.platform.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Entity
@Table(name = "profiles")
@Data
@Builder
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String email;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
}
