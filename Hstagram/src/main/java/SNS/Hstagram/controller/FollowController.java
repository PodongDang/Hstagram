package SNS.Hstagram.controller;

import SNS.Hstagram.service.FollowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/follow")
@RequiredArgsConstructor
@Tag(name = "Follow API", description = "팔로우 관련 API")
public class FollowController {

    private final FollowService followService;

    @PostMapping("/{followingId}")
    @Operation(summary = "팔로우", description = "특정 사용자를 팔로우합니다.")
    public ResponseEntity<String> followUser(@RequestParam("followerId") Long followerId,
                                             @PathVariable("followingId") Long followingId) {
        followService.followUser(followerId, followingId);
        return ResponseEntity.ok("팔로우 완료");
    }

    @DeleteMapping("/{followingId}")
    @Operation(summary = "언팔로우", description = "특정 사용자 팔로우를 취소합니다.")
    public ResponseEntity<String> unfollowUser(@RequestParam("followerId") Long followerId,
                                               @PathVariable("followingId") Long followingId) {
        followService.unfollowUser(followerId, followingId);
        return ResponseEntity.ok("언팔로우 완료");
    }

    @GetMapping("/{userId}/followers")
    @Operation(summary = "팔로워 조회", description = "특정 사용자의 팔로워 목록을 조회합니다.")
    public ResponseEntity<?> getFollowers(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(followService.getFollowers(userId));
    }

    @GetMapping("/{userId}/following")
    @Operation(summary = "팔로우 목록 조회", description = "특정 사용자가 팔로우하는 사용자 목록을 조회합니다.")
    public ResponseEntity<?> getFollowing(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(followService.getFollowing(userId));
    }
}
