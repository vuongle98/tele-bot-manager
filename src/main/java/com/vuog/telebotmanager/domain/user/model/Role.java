package com.vuog.telebotmanager.domain.user.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "roles")
@Getter @Setter
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    public Role() {}
    
    public Role(String name) {
        this.name = name;
    }
}
