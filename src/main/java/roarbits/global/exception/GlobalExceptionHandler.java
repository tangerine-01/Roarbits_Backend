package roarbits.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.dao.DataIntegrityViolationException;
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

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        String rootMsg = e.getMostSpecificCause() != null ? e.getMostSpecificCause().getMessage() : e.getMessage();
        log.warn("DataIntegrityViolationException: {}", rootMsg);

        boolean emailUnique =
                rootMsg != null &&
                        (rootMsg.toLowerCase().contains("uk_users_email")
                                || rootMsg.toLowerCase().contains("users.email")
                                || rootMsg.toLowerCase().contains("duplicate")
                                || rootMsg.toLowerCase().contains("unique"));

        String code = emailUnique ? "DUPLICATE_EMAIL" : "CONSTRAINT_VIOLATION";
        String field = emailUnique ? "email" : null;
        String msg = emailUnique ? "이미 가입된 이메일입니다." : "중복된 값 또는 제약 조건 위반으로 요청이 실패했습니다.";

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(code, msg, field));
    }

    @ExceptionHandler(SubjectNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleSubjectNotFound(SubjectNotFoundException e) {
        BaseCode errorCode = e.getErrorCode();
        return ResponseEntity
                .status(errorCode.getReason().getHttpStatus())
                .body(ApiResponse.onFailure(errorCode, null));
    }
}
