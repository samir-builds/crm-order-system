package com.samir.crm_order_system.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotNull(message = "Username can not be null")
    @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
    public String username;

    @Email(message = "Email must be valid")
    public String email;

    @NotNull(message = "Password can not be null")
    @Size(min = 6 , message = "Password must be at least 6 characters")
    public String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @Override
    public String toString() {
        return String.format(
                "İstifadəçi ID{id=%d, İstifadəçi adı='%s', Email='%s', Rollar=%s}",
                id,
                username,
                email,
                roles != null ? roles.toString() : "[]"
        );
    }

}
