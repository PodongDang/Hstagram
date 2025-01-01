package SNS.Hstagram.service;

import SNS.Hstagram.domain.Follow;
import SNS.Hstagram.domain.User;
import SNS.Hstagram.repository.FollowRepository;
import SNS.Hstagram.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    // 팔로우 요청
    public void followUser(Long followerId, Long followingId) {
        User follower = userRepository.findById(followerId);
        User following = userRepository.findById(followingId);

        if (follower == null || following == null) {
            throw new EntityNotFoundException("User not found");
        }

        // 중복 팔로우 방지
        if (followRepository.findByFollowerAndFollowing(followerId, followingId) == null) {
            Follow follow = new Follow();
            follow.setFollower(follower);
            follow.setFollowing(following);
            follow.setStatus("PENDING");
            followRepository.save(follow);
        } else {
            throw new IllegalStateException("Already following this user");
        }
    }

    // 언팔로우
    public void unfollowUser(Long followerId, Long followingId) {
        Follow follow = followRepository.findByFollowerAndFollowing(followerId, followingId);
        if (follow != null) {
            followRepository.delete(follow);
        } else {
            throw new EntityNotFoundException("Follow relationship not found");
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
