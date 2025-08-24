package roarbits.timetable.service;

import roarbits.subject.entity.Subject;
import roarbits.subject.repository.SubjectRepository;
import roarbits.timetable.entity.*;
import roarbits.timetable.dto.*;
import roarbits.timetable.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roarbits.user.entity.User;
import roarbits.user.repository.UserRepository;

import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TimetableService {
    private final TimetableRepository timetableRepository;
    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;
    private final TimeSlotRepository timeSlotRepository;

    // 시간표 생성
    public TimetableResponseDto createTimetable(Long userId, TimetableRequestDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다.(사용자 ID 잘못됨)"));

        Timetable timetable = Timetable.builder()
                .user(user)
                .preferCredit(dto.getPreferCredit())
                .preferTime(dto.getPreferTime())
                .morningClassNum(dto.getMorningClassNum())
                .freePeriodNum(dto.getFreePeriodNum())
                .essentialCourse(dto.getEssentialCourse())
                .graduationRate(dto.getGraduationRate())
                .category(dto.getCategory())
                .build();

        List<TimeSlot> timeSlots = dto.getTimeSlots().stream().map(slotDto -> {
            Subject subject = subjectRepository.findById(slotDto.getSubjectId())
                    .orElseThrow(() -> new IllegalArgumentException("과목을 찾을 수 없습니다."));

            return TimeSlot.builder()
                    .timetable(timetable)
                    .subject(subject)
                    .day(slotDto.getDay())
                    .startTime(LocalTime.parse(slotDto.getStartTime()))
                    .endTime(LocalTime.parse(slotDto.getEndTime()))
                    .build();
        }).toList();

        timetable.setTimeSlots(timeSlots);
        return toResponseDto(timetableRepository.save(timetable));
    }

    // 시간표 단일 조회
    @Transactional
    public TimetableResponseDto getTimetable(Long userId, Long timetableId) {
        Timetable t = timetableRepository.findByTimetableIdAndUser_Id(timetableId, userId)
                .orElseThrow(() -> new IllegalArgumentException("시간표를 찾을 수 없습니다."));
        return toResponseDto(t);
    }

    // 사용자별 시간표 조회
    @Transactional
    public List<TimetableResponseDto> getTimetablesByUser(Long Id) {
        return timetableRepository.findAllByUser_Id(Id).stream()
                .map(this::toResponseDto)
                .toList();
    }

    // 메인 시간표 설정
    public TimetableResponseDto setMainTimetable(Long userId, Long timetableId) {
        Timetable tt = timetableRepository.findByTimetableIdAndUser_Id(timetableId, userId)
                .orElseThrow(
                        () -> new IllegalArgumentException("시간표를 찾을 수 없습니다."));

        // 기존 메인 시간표 해제
        timetableRepository.clearMainByUserId(userId);

        // 새로운 메인 시간표 설정
        tt.setMain(true);

        return toResponseDto(tt);
    }

    // 메인 시간표 조회
    public TimetableResponseDto getMainTimetable(Long userId) {
        Timetable tt = timetableRepository.findByUser_IdAndIsMainTrue(userId)
                .orElseThrow(() -> new IllegalArgumentException("메인 시간표가 설정되어 있지 않습니다."));
        return toResponseDto(tt);
    }

    // 시간표 삭제
    public void deleteTimetable(Long userId, Long timetableId) {
        long n = timetableRepository.deleteByTimetableIdAndUser_Id(timetableId, userId);
        if (n == 0) throw new IllegalArgumentException("권한이 없거나 존재하지 않는 시간표입니다.");
    }

    // 엔티티 -> DTO 변환
    private TimetableResponseDto toResponseDto(Timetable timetable) {
        List<TimeSlotDto> slotDtos = timetable.getTimeSlots().stream().map(slot -> {
            Subject subject = slot.getSubject();

            String category = subject.getCategory();
            if (category == null || category.isEmpty()) {
                category = "GENERAL";
            }

            return TimeSlotDto.builder()
                    .subjectId(subject.getId())
                    .subjectName(subject.getName())
                    .courseType(subject.getCourseType())
                    .discipline(subject.getDiscipline())
                    .classroom(subject.getClassroom())
                    .professor(subject.getProfessor())
                    .credit(subject.getCredit())
                    .startTime(slot.getStartTime().toString())
                    .endTime(slot.getEndTime().toString())
                    .day(slot.getDay())
                    .category(category)
                    .build();
        }).toList();

        return TimetableResponseDto.builder()
                .timetableId(timetable.getTimetableId())
                .userId(timetable.getUser().getId())
                .preferCredit(timetable.getPreferCredit())
                .preferTime(timetable.getPreferTime())
                .morningClassNum(timetable.getMorningClassNum())
                .freePeriodNum(timetable.getFreePeriodNum())
                .essentialCourse(timetable.getEssentialCourse())
                .graduationRate(timetable.getGraduationRate())
                .timeSlots(slotDtos)
                .build();
    }}
