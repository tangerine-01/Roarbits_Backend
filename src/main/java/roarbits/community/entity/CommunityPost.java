package roarbits.community.entity;

import roarbits.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "community_post",
        indexes = {@Index(name = "idx_post_lat_lng", columnList = "lat, lng")
    })
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 1000)
    private String content;

    private boolean isDeleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User writer;

    @Enumerated(EnumType.STRING)
    private PostType type;

    // 위치, 시간, 모집 인원
    @Column(precision = 10, scale = 7)
    private Double lat;

    @Column(precision = 10, scale = 7)
    private Double lng;

    private LocalDateTime meetTime;

    private Integer maxParticipants;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommunityComment> comments;

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean Deleted) {
        isDeleted = Deleted;
    }
}