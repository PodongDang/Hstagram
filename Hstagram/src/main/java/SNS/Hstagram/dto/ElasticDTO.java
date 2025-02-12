package SNS.Hstagram.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class ElasticDTO {
    private Long postId;
    private Long userId;
    private String content;
    private String imageUrl;
    private int likeCount;
    private int commentCount;
    private boolean isPrivate;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
