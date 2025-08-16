package roarbits.notification.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_interest",
        uniqueConstraints = @UniqueConstraint(
            name = "unique_user_interest",
            columnNames = {"user_id", "subject_id"}
        ),
        indexes = {
            @Index(name = "idx_user_interest_user", columnList = "user_id"),
            @Index(name = "idx_user_interest_subject", columnList = "subject_id")
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

    @JoinColumn(name = "subject_id", nullable = false)
    private Long subjectId;
}
