package SNS.Hstagram.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "follow")
public class Follow {

    @EmbeddedId
    private FollowId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("followerId")  // FollowId의 followerId에 매핑
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("followingId")  // FollowId의 followingId에 매핑
    @JoinColumn(name = "following_id", nullable = false)
    private User following;

    private LocalDateTime followedAt = LocalDateTime.now();

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "notification_enabled", nullable = false)
    private Boolean notificationEnabled = true;

    // equals, hashCode override (팔로우 중복 방지)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Follow follow = (Follow) o;
        return Objects.equals(follower, follow.follower) &&
                Objects.equals(following, follow.following);
    }

    @Override
    public int hashCode() {
        return Objects.hash(follower, following);
    }


}
