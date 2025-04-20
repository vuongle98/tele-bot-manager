package com.vuog.telebotmanager.domain.user.model;

import com.vuog.telebotmanager.domain.bot.model.TelegramBot;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
    
    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
    private Set<TelegramBot> bots = new HashSet<>();
    
    public boolean hasRole(String roleName) {
        return roles.stream().anyMatch(role -> role.getName().equals(roleName));
    }
}

// Note: The UserRole enum has been replaced with a Role entity, 
// which is not provided in the given code edit. 
// You may need to create a separate entity for Role.
