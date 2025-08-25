package roarbits.ai.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import roarbits.community.dto.CommunityRequestDto;
import roarbits.community.dto.CommunityResponseDto;
import roarbits.community.service.CommunityService;
import roarbits.community.entity.PostType;  // ★ import

import java.util.Arrays;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class AiPublishFacade {
    private final CommunityService communityService;

    private PostType parsePostType(String s) {
        if (s == null) throw new IllegalArgumentException("postType is null");
        try {
            return PostType.valueOf(s.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "postType must be one of " + Arrays.toString(PostType.values())
            );
        }
    }

    public CommunityResponseDto.Post publish(Long writerId, String title, String content, String postTypeStr) {
        PostType postType = parsePostType(postTypeStr);

        // 빌더 쓰는 버전
        CommunityRequestDto.CreatePost req = CommunityRequestDto.CreatePost.builder()
                .title(title)
                .content(content)
                .type(postType)   // ★ enum 주입
                .build();

        return communityService.createPost(writerId, req);
    }
}