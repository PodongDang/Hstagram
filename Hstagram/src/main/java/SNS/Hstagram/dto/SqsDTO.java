package SNS.Hstagram.dto;

import lombok.Data;

@Data
public class SqsDTO {
    private Long postId;
    private Long userId;
}
