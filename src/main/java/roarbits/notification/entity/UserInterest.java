package roarbits.notification.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_interest",
        uniqueConstraints = @UniqueConstraint(
            name = "unique_user_interest",
            columnNames = {"user_id", "interest_type", "interest_target_id"}
        ),
        indexes = {
            @Index(name = "idx_user_interest_user", columnList = "user_id"),
            @Index(name = "idx_user_interest_type_target", columnList = "interest_type, interest_target_id")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserInterest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "interest_type", nullable = false, length = 50)
    private String interestType;

    @Column(name = "interest_target_id", nullable = false)
    private Long interestTargetId;

    @Builder.Default
    @Column(nullable = false)
    private boolean enabled = false;

    //알림수신 여부 변경 메서드
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void toggleEnabled() {
        this.enabled = !this.enabled;
    }
}
