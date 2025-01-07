package SNS.Hstagram.service;

import SNS.Hstagram.domain.Follow;
import SNS.Hstagram.domain.FollowId;
import SNS.Hstagram.domain.Post;
import SNS.Hstagram.domain.User;
import SNS.Hstagram.repository.FollowRepository;
import SNS.Hstagram.repository.PostRepository;
import SNS.Hstagram.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final RedisTemplate<String, Long> redisLongTemplate;

    // 팔로우 요청
    public void followUser(Long followerId, Long followingId) {
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new EntityNotFoundException("Follower not found"));
        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new EntityNotFoundException("Following user not found"));

        // 중복 팔로우 방지
//        followRepository.findByFollowerAndFollowing(followerId, followingId)
//                .ifPresent(f -> { throw new IllegalStateException("Already following this user"); });

        // FollowId 초기화
        FollowId followId = new FollowId(followerId, followingId);
        Follow follow = new Follow();

        // Follow 엔티티에 FollowId 명시적으로 설정
        follow.setId(followId);
        follow.setFollower(follower);
        follow.setFollowing(following);
        follow.setStatus("PENDING");
        follow.setNotificationEnabled(true);

        try {
            followRepository.save(follow);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("Already following this user.");
        }

        // 팔로우 대상 게시글을 조회하여 피드 업데이트
        updateUserFeed(followerId);
    }

    // 언팔로우
    public void unfollowUser(Long followerId, Long followingId) {
        Follow follow = followRepository.findByFollowerAndFollowing(followerId, followingId)
                .orElseThrow(() -> new EntityNotFoundException("Follow relationship not found"));
        followRepository.delete(follow);

        // 언팔로우 이후 피드 업데이트
        updateUserFeed(followerId);
    }

    // Redis 피드 업데이트
    private void updateUserFeed(Long userId) {
        // 팔로우한 사용자의 게시글 조회 (fetch join)
        List<Post> posts = postRepository.findFeedPostsByUserId(userId);

        // Redis에서 기존 피드 삭제
        String feedKey = "feed:" + userId;
        redisLongTemplate.delete(feedKey);

        // 게시글 ID 목록을 Redis에 푸시 (Long 타입)
        List<Long> postIds = posts.stream()
                .map(Post::getId)
                .collect(Collectors.toList());

        if (!postIds.isEmpty()) {
            redisLongTemplate.opsForList().leftPushAll(feedKey, postIds.toArray(new Long[0]));
            redisLongTemplate.opsForList().trim(feedKey, 0, 99);  // 최신 100개 유지
        }
    }

    // 팔로워 목록 조회
    public List<Follow> followersList(Long userId) {
        return followRepository.findFollowers(userId);
    }

    // 팔로우 목록 조회
    public List<Follow> followingList(Long userId) {
        return followRepository.findFollowing(userId);
    }
}
