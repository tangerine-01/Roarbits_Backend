package roarbits.ai.prompt;

public class SchedulePromptBuilder {
    public static String build(String scheduleJson) {
        return String.format(
                "가져온 사용자의 scheduleJson은 다음과 같습니다:\n%s\n" +
                "이 시간표를 기반으로 학생에게 맞는 추천을 해주세요.\n" +
                "예: 택시팟 모으기, 점심 같이 먹을 사람 구하기, 공강 시간 활용 등.\n" +
                "추천은 간단한 문장으로 작성해주세요.",
                scheduleJson
        );
    }
}
