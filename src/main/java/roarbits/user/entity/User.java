package roarbits.user.entity;

import jakarta.persistence.*;
import roarbits.user.entity.BaseEntity;
import roarbits.login.auth.jwt.RefreshToken;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String nickname;
}
