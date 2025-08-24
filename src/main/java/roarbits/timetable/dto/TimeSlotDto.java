package roarbits.timetable.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeSlotDto {
    @NotNull(message = "subjectId는 필수입니다.")
    private Long subjectId; // 과목 ID

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String subjectName; // 과목 이름

    @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "HH:mm 형식(00-23시)이어야 합니다. (예: 09:00)")
    private String startTime; // 시간 (예: "09:00")

    @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "HH:mm 형식(00-23시)이어야 합니다. (예: 10:30)")
    private String endTime; // 시간 (예: "10:00")

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String classroom; // 강의실 (예: "전자관 101호")

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String professor; // 교수님 이름

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Integer credit; // 학점

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String courseType; // 이수구분 (예: "전공선택", "교양필수")

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String discipline;  // 교양과정 구분 (예: "철학과 역사", "소양")

    @Min(value = 0, message = "요일은 0(월요일)부터 6(일요일)까지의 값이어야 합니다.")
    @Max(value = 6, message = "요일은 0(월요일)부터 6(일요일)까지의 값이어야 합니다.")
    private Integer day; // 요일 (0: 월요일, 1: 화요일, ..., 6: 일요일)

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String category; // 과목 카테고리 (예: "전공", "교양")
}
