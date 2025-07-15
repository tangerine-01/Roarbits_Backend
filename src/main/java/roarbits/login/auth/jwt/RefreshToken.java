package roarbits.login.auth.jwt;

import jakarta.persistence.*;
import lombok.*;
import roarbits.user.entity.User;
import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token; // 실제 Refresh Token 값

    // ⭐ User 엔티티를 참조 (ManyToOne 관계)
    // RefreshToken은 특정 User에 속하지만, User는 여러 RefreshToken을 가질 수 있습니다.
    @OneToOne(fetch = FetchType.LAZY) // 지연 로딩
    @JoinColumn(name = "user_id", nullable = false) // user_id 컬럼으로 매핑
    private User user;

    @Column(nullable = false)
    private LocalDateTime expiryDate; // 토큰 만료 시간
}