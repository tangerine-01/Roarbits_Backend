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

    @GetMapping("/id/{subjectId}")
    public ApiResponse<SubjectDto> getSubjectBySubjectId(@PathVariable String subjectId) {
        SubjectDto subject = subjectService.getSubjectBySubjectId(subjectId);
        return ApiResponse.onSuccess(SuccessCode.SUBJECT_DETAIL_SUCCESS, subject);
    }

    @GetMapping("/search")
    public ApiResponse<List<SubjectDto>> searchSubjects(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String professor) {

        List<SubjectDto> result;

        if (name != null) {
            result = subjectService.searchSubjectsByName(name);
        } else if (professor != null) {
            result = subjectService.searchSubjectsByProfessor(professor);
        } else {
            result = List.of();
        }

        return ApiResponse.onSuccess(SuccessCode.SUBJECT_SEARCH_SUCCESS, result);
    }
}
