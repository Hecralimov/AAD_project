package com.ecommerce.analytics_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "role_type")
    private String roleType; // Admin, Corporate, Individual

    @Column(name = "is_active")
    private Boolean isActive;

    private String gender;

    // --- UserDetails Methods for Spring Security ---

    public void setActive(boolean active) {
        this.isActive = active;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // We add "ROLE_" prefix because Spring Security usually expects it for RBAC
        return List.of(new SimpleGrantedAuthority("ROLE_" + roleType.toUpperCase()));
    }

    @Override
    public String getPassword() {
        return passwordHash; // Tell Spring Security where to find the password
    }

    @Override
    public String getUsername() {
        return email; // We use email as the username for login
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return !Boolean.FALSE.equals(isActive);
    }

    public String getId() {
        return id;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

}
