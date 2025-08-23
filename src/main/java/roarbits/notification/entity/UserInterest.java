package roarbits.notification.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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

    @Builder.Default
    @Column(name = "interest_type", nullable = false, length = 50)
    private String interestType = "SUBJECT";

    @Builder.Default
    @Column(name="enabled", nullable=false)
    private boolean enabled = true;

    @Builder.Default
    @Column(name="created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    void prePersist() {
        if (interestType == null || interestType.isBlank()) interestType = "SUBJECT";
        if (createdAt == null) createdAt = java.time.LocalDateTime.now();
    }
}
