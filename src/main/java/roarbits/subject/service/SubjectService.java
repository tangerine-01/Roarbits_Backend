package roarbits.subject.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roarbits.subject.entity.Subject;
import roarbits.subject.repository.SubjectRepository;
import org.springframework.data.domain.Sort;
import roarbits.subject.dto.SubjectDto;
import java.util.stream.Collectors;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository subjectRepository;

    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    public Subject getSubjectById(Long id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("과목을 찾을 수 없습니다."));
    }

    public List<SubjectDto> getAllSubjectsSorted(String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        List<Subject> subjects = subjectRepository.findAll(sort);

        return subjects.stream()
                .map(SubjectDto::from)
                .collect(Collectors.toList());
    }
}
