package roarbits.subject.exception;

import roarbits.global.api.BaseCode;
import roarbits.global.api.Errorcode;
import roarbits.global.api.Errorcode;

public class SubjectNotFoundException extends RuntimeException {
    private final BaseCode errorCode;

    public SubjectNotFoundException() {
        super("해당 과목을 찾을 수 없습니다.");
        this.errorCode = Errorcode.SUBJECT_NOT_FOUND;
    }

    public BaseCode getErrorCode() {
        return errorCode;
    }
}
