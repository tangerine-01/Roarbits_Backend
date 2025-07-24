package roarbits.graduation.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "graduation_requirements")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class GraduationRequirement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String major;

    private int totalCredits;
    private int majorCredits;
    private int electiveCredits;
    private int generalCredits;
    private boolean requiresEnglishTest;
    private String etc;

    public void update(String major, int totalCredits, int majorCredits,
                       int electiveCredits, int generalCredits,
                       boolean requiresEnglishTest, String etc) {
        this.major = major;
        this.totalCredits = totalCredits;
        this.majorCredits = majorCredits;
        this.electiveCredits = electiveCredits;
        this.generalCredits = generalCredits;
        this.requiresEnglishTest = requiresEnglishTest;
        this.etc = etc;
    }
}
