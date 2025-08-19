package roarbits.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Collection;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import roarbits.user.entity.Profile;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
@Builder
@AllArgsConstructor
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    private String email;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 100)
    private String name;

    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name="user_roles",
            joinColumns=@JoinColumn(name="user_id"),
            uniqueConstraints = @UniqueConstraint(name = "uk_user_roles", columnNames = {"user_id","role"})
    )
    @Column(name="role", nullable = false)
    private List<String> roles = List.of("ROLE_USER");

    @OneToOne(mappedBy = "user",
                cascade = CascadeType.ALL,
                orphanRemoval = true,
                fetch = FetchType.LAZY)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Profile profile;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return email;
    }
    public Long getId() {
        return id;
    }

    @Column(nullable = false)
    private boolean withdrawn = false;
    private LocalDateTime withdrawnAt;

    @Override
    public String getPassword() {
        return password;
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
        return true;
    }
}
