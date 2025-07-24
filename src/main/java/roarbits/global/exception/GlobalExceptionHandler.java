package roarbits.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import roarbits.global.api.ApiResponse;
import roarbits.global.api.BaseCode;
import roarbits.subject.exception.SubjectNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SubjectNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleSubjectNotFound(SubjectNotFoundException e) {
        BaseCode errorCode = e.getErrorCode();
        return ResponseEntity
                .status(errorCode.getReason().getHttpStatus())
                .body(ApiResponse.onFailure(errorCode, null));
    }
}
