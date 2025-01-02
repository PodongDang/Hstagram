package SNS.Hstagram.dto;

import SNS.Hstagram.domain.Post;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public record PostDTO (
    Long id,
    String content,
    String imageUrl,
    LocalDateTime createdAt
){
    public static PostDTO from(Post post) {
        return PostDTO.builder()
                .id(post.getId())
                .content(post.getContent())
                .imageUrl(post.getImageUrl())
                .createdAt(post.getCreatedAt())
                .build();
    }
}
