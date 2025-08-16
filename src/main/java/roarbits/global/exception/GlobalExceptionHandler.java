package roarbits.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import roarbits.global.api.ApiResponse;
import roarbits.global.api.BaseCode;
import roarbits.login.exception.DuplicateEmailException;
import roarbits.subject.exception.SubjectNotFoundException;

record ErrorResponse(String code, String message, String field) {}

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateEmailException(DuplicateEmailException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("DUPLICATE_EMAIL", e.getMessage(), "email"));
    }

    @ExceptionHandler(SubjectNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleSubjectNotFound(SubjectNotFoundException e) {
        BaseCode errorCode = e.getErrorCode();
        return ResponseEntity
                .status(errorCode.getReason().getHttpStatus())
                .body(ApiResponse.onFailure(errorCode, null));
    }
}
