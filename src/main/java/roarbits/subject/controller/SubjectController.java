package roarbits.subject.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import roarbits.global.api.ApiResponse;
import roarbits.global.api.SuccessCode;
import roarbits.subject.entity.Subject;
import roarbits.subject.service.SubjectService;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    @GetMapping
    public ApiResponse<List<Subject>> getAllSubjects() {
        List<Subject> subjects = subjectService.getAllSubjects();
        return ApiResponse.onSuccess(SuccessCode.SUBJECT_LIST_SUCCESS, subjects);
    }
}
