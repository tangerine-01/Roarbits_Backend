package roarbits.subject.dto;

import lombok.*;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectProgressResponseDto {
    private Long userId;
    private long totalCredits;
    private List<CategoryItem> categories;
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryItem {
        private String category;
        private long credits;
    }

}
