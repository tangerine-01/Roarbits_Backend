package roarbits.login.auth.jwt;

import jakarta.persistence.*;
import lombok.*;
import roarbits.user.entity.User;
import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_tokens",
uniqueConstraints = {
        @UniqueConstraint(name = "uk_refresh_user", columnNames = "user_id"),
        @UniqueConstraint(name = "uk_refresh_token",columnNames = "token")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 500)
    private String token;


    @Column(nullable = false)
    private LocalDateTime expiryDate;

    public void rotate(String newToken, LocalDateTime newExpiry) {
        this.token = newToken;
        this.expiryDate = newExpiry;
    }

}