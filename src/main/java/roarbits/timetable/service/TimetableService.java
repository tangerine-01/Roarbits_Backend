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
import roarbits.timetable.entity.TimeSlot;

import java.time.LocalTime;
import java.util.*;

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
                .isMain(!timetableRepository.existsByUser_IdAndIsMainTrue(userId)) // 첫 시간표면 메인으로 설정
                .build();

        var timeSlots = dto.getTimeSlots().stream().map(slotDto -> {
            var subject = subjectRepository.findById(slotDto.getSubjectId())
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
                .orElseThrow(() -> new IllegalArgumentException("시간표를 찾을 수 없습니다."));

        if (Boolean.TRUE.equals(tt.isMain())) {
            return toResponseDto(tt); // 이미 메인인 경우 그대로 반환
        }

        timetableRepository.clearMainByUserId(userId);
        tt.setMain(true);

        return toResponseDto(tt);
    }

    // 메인 시간표 조회
    @Transactional
    public TimetableResponseDto getMainTimetable(Long userId) {
        Timetable tt = timetableRepository.findByUser_IdAndIsMainTrue(userId)
                .orElseThrow(() -> new IllegalArgumentException("메인 시간표가 설정되어 있지 않습니다."));
        return toResponseDto(tt);
    }

    public void activateTimetable(Long userId, Long timetableId) {
                timetableRepository.clearActive(userId);
                Timetable t = timetableRepository.findByIdAndUserId(timetableId, userId)
                                .orElseThrow(() -> new IllegalArgumentException("시간표가 없거나 권한 없음"));
                t.activate();
            }

    public Optional<TimetableResponseDto> getMainTimetableOptional(Long userId) {
        Timetable t =
                timetableRepository.findMainWithSlotsByUserId(userId).orElse(null);
        if (t == null) {
            return Optional.empty();
        }

        List<TimeSlotDto> slots =
                (t.getTimeSlots() == null ? java.util.List.<roarbits.timetable.entity.TimeSlot>of() : t.getTimeSlots())
                        .stream()
                        .map(TimeSlotDto::fromEntity)
                        .toList();

        return Optional.of(TimetableResponseDto.fromEntity(t, slots));
    }

    // 시간표 삭제
    public void deleteTimetable(Long userId, Long timetableId) {
        Timetable tt = timetableRepository.findByTimetableIdAndUser_Id(timetableId, userId)
                .orElseThrow(() -> new IllegalArgumentException("시간표를 찾을 수 없습니다."));

        boolean wasMain = Boolean.TRUE.equals(tt.isMain());
        timeSlotRepository.deleteByTimetable(tt);
        timetableRepository.delete(tt);

        // 메인 시간표였으면 다른 시간표를 메인으로 설정
        if (wasMain) {
            timetableRepository.findFirstByUser_IdOrderByTimetableIdDesc(userId).ifPresent(next -> next.setMain(true));
        }
    }

    // 엔티티 -> DTO 변환
    private TimetableResponseDto toResponseDto(Timetable timetable) {
        List<TimeSlotDto> slotDtos = timetable.getTimeSlots().stream().map(slot -> {
            Subject subject = slot.getSubject();

            String category = subject.getCategory();
            if (category == null || category.isEmpty()) {
                category = "GENERAL";
            }

            String description = subject.getDiscipline();
            if (description == null || description.isBlank()) {
                description = "기타";
            }

            return TimeSlotDto.builder()
                    .subjectId(subject.getId())
                    .subjectName(subject.getName())
                    .courseType(subject.getCourseType())
                    .description(description)
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
