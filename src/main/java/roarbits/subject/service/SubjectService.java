package roarbits.subject.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roarbits.subject.entity.Subject;
import roarbits.subject.repository.SubjectRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository subjectRepository;

    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }
}
