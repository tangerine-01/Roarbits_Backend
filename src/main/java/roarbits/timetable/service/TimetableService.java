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
    public TimetableResponseDto createTimetable(TimetableRequestDto dto) {
        User user = userRepository.findById(dto.getUserId())
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
        }).collect(Collectors.toList());

        timetable.setTimeSlots(timeSlots);
        Timetable saved = timetableRepository.save(timetable);

        return toResponseDto(saved);
    }

    // 시간표 단일 조회
    public TimetableResponseDto getTimetable(Long timetableId) {
        Timetable timetable = timetableRepository.findById(timetableId)
                .orElseThrow(() -> new IllegalArgumentException("시간표를 찾을 수 없습니다."));
        return toResponseDto(timetable);
    }

    // 사용자별 시간표 조회
    public List<TimetableResponseDto> getTimetablesByUser(Long userId) {
        return timetableRepository.findByUserId(userId).stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    // 시간표 수정
    public TimetableResponseDto updateTimetable(Long timetableId, TimetableRequestDto dto) {
        Timetable timetable = timetableRepository.findById(timetableId)
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
        }).collect(Collectors.toList());

        timetable.setTimeSlots(updatedSlots);
        return toResponseDto(timetable);
    }

    // 시간표 삭제
    public void deleteTimetable(Long timetableId) {
        Timetable timetable = timetableRepository.findById(timetableId)
                .orElseThrow(() -> new IllegalArgumentException("시간표를 찾을 수 없습니다."));
        timetableRepository.delete(timetable);
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
        }).collect(Collectors.toList());

        return TimetableResponseDto.builder()
                .timetableId(timetable.getTimetableId())
                .userId(timetable.getUser().getUserId())
                .preferCredit(timetable.getPreferCredit())
                .preferTime(timetable.getPreferTime())
                .morningClassNum(timetable.getMorningClassNum())
                .freePeriodNum(timetable.getFreePeriodNum())
                .essentialCourse(timetable.getEssentialCourse())
                .graduationRate(timetable.getGraduationRate())
                .timeSlots(slotDtos)
                .build();
    }}
