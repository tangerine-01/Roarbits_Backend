package roarbits.coursehistory.dto;

import static roarbits.coursehistory.dto.CourseEnums.*;

public class CourseEnums {
    public enum Semester { FIRST, SECOND }
    public enum RetakeType { ORIGINAL, RETAKE }
    Semester s = Semester.FIRST;
    RetakeType r = RetakeType.RETAKE;
}

