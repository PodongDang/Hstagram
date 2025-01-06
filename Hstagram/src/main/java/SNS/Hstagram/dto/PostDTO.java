package SNS.Hstagram.dto;

import SNS.Hstagram.domain.Post;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PostDTO (
    Long id,
    String content,
    String imageUrl
    //LocalDateTime createdAt
){
    public static PostDTO from(Post post) {
        return PostDTO.builder()
                .id(post.getId())
                .content(post.getContent())
                .imageUrl(post.getImageUrl())
                //.createdAt(post.getCreatedAt())
                .build();
    }
}
