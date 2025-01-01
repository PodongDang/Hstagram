package SNS.Hstagram.dto;

import SNS.Hstagram.domain.Post;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostDTO {
    private Long id;
    private String content;
    private LocalDateTime createdAt;

    public PostDTO(Post post) {
        this.id = post.getId();
        this.content = post.getContent();
        this.createdAt = post.getCreatedAt();
    }
}