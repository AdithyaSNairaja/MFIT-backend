package com.aja.ott.entity;

import jakarta.persistence.*; 
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;
    
    private String password;

    @Column(unique = true, nullable = false)
    private String phone;

    private String role;
    private String department;
    
    private boolean forcePasswordChange;

    @Column(nullable = false)
    private boolean isDeleted = false;
}
