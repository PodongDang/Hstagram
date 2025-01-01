package SNS.Hstagram.dto;

import SNS.Hstagram.domain.Post;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostDTO {
    private Long id;
    private String content;
    private String imageUrl;
    private LocalDateTime createdAt;

    public PostDTO(Post post) {
        this.id = post.getId();
        this.content = post.getContent();
        this.imageUrl = post.getImageUrl();
        this.createdAt = post.getCreatedAt();
    }
}
