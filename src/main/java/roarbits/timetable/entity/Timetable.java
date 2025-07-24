package roarbits.timetable.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.ArrayList;

import roarbits.user.entity.User;
import roarbits.timetable.entity.TimeSlot;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Timetable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long timetableId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "timetable", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TimeSlot> timeSlots = new ArrayList<>();

    // 편의 메서드
    public void addTimeSlot(TimeSlot timeSlot) {
        timeSlots.add(timeSlot);
        timeSlot.setTimetable(this);
    }

    public void removeTimeSlot(TimeSlot timeSlot) {
        timeSlots.remove(timeSlot);
        timeSlot.setTimetable(null);
    }

    private Integer preferCredit;
    private String preferTime;
    private Integer morningClassNum;
    private Integer freePeriodNum;
    private String essentialCourse;
    private Double graduationRate;
}
