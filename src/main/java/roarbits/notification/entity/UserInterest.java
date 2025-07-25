package roarbits.notification.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "user_interest")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInterest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String interestType;
    private Long interestTargetId;
    private boolean enabled;

    //알림수신 여부 변경 메서드
    public void setEnabled(boolean enabled) {
        this.enabled = !this.enabled;
    }
}
