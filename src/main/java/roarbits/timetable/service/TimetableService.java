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
import java.util.stream.Collectors;

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
        Timetable t = timetableRepository.findByTimetableIdAndUser_UserId(timetableId, userId)
                .orElseThrow(() -> new IllegalArgumentException("시간표를 찾을 수 없습니다."));
        return toResponseDto(t);
    }

    // 사용자별 시간표 조회
    @Transactional
    public List<TimetableResponseDto> getTimetablesByUser(Long userId) {
        return timetableRepository.findAllByUser_UserId(userId).stream()
                .map(this::toResponseDto)
                .toList();
    }

    // 시간표 수정
    public TimetableResponseDto updateTimetable(Long userId, Long timetableId, TimetableRequestDto dto) {
        Timetable timetable = timetableRepository.findByTimetableIdAndUser_UserId(timetableId, userId)
                .orElseThrow(() -> new IllegalArgumentException("시간표를 찾을 수 없습니다."));

        timetable.setPreferCredit(dto.getPreferCredit());
        timetable.setPreferTime(dto.getPreferTime());
        timetable.setMorningClassNum(dto.getMorningClassNum());
        timetable.setFreePeriodNum(dto.getFreePeriodNum());
        timetable.setEssentialCourse(dto.getEssentialCourse());
        timetable.setGraduationRate(dto.getGraduationRate());

        // 기존 슬롯 제거 후 새로 설정
        timeSlotRepository.deleteByTimetable(timetable);

        List<TimeSlot> updatedSlots = dto.getTimeSlots().stream().map(slotDto -> {
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

        timetable.setTimeSlots(updatedSlots);
        return toResponseDto(timetable);
    }

    // 시간표 삭제
    public void deleteTimetable(Long userId, Long timetableId) {
        long n = timetableRepository.deleteByTimetableIdAndUser_UserId(timetableId, userId);
        if (n == 0) throw new IllegalArgumentException("권한이 없거나 존재하지 않는 시간표입니다.");
    }

    // 엔티티 -> DTO 변환
    private TimetableResponseDto toResponseDto(Timetable timetable) {
        List<TimeSlotDto> slotDtos = timetable.getTimeSlots().stream().map(slot -> {
            Subject subject = slot.getSubject();
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
