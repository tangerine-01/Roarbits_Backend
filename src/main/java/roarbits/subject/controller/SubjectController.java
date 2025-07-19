package roarbits.subject.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import roarbits.global.api.ApiResponse;
import roarbits.global.api.SuccessCode;
import roarbits.subject.dto.SubjectDto;
import roarbits.subject.entity.Subject;
import roarbits.subject.service.SubjectService;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    @GetMapping
    public ApiResponse<List<SubjectDto>> getAllSubjects(
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        List<SubjectDto> subjects = subjectService.getAllSubjectsSorted(sortBy, direction);
        return ApiResponse.onSuccess(SuccessCode.SUBJECT_LIST_SUCCESS, subjects);
    }

    @GetMapping("/{id}")
    public ApiResponse<Subject> getSubjectById(@PathVariable Long id) {
        Subject subject = subjectService.getSubjectById(id);
        return ApiResponse.onSuccess(SuccessCode.SUBJECT_DETAIL_SUCCESS, subject);
    }
}
