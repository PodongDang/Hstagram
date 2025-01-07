package SNS.Hstagram.domain;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class FollowId implements Serializable {

    private Long followerId;  // 팔로우하는 사용자 ID
    private Long followingId;  // 팔로우 대상 사용자 ID
}
