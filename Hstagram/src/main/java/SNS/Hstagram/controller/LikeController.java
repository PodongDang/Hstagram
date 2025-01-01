package SNS.Hstagram.controller;

import SNS.Hstagram.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
@Tag(name = "Like API", description = "좋아요 관련 API")
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/post/{postId}")
    @Operation(summary = "게시글 좋아요", description = "게시글에 좋아요를 추가합니다.")
    public ResponseEntity<String> likePost(@RequestParam Long userId, @PathVariable Long postId) {
        likeService.likePost(userId, postId);
        return ResponseEntity.ok("게시글 좋아요 완료");
    }

    @PostMapping("/comment/{commentId}")
    @Operation(summary = "댓글 좋아요", description = "댓글에 좋아요를 추가합니다.")
    public ResponseEntity<String> likeComment(@RequestParam Long userId, @PathVariable Long commentId) {
        likeService.likeComment(userId, commentId);
        return ResponseEntity.ok("댓글 좋아요 완료");
    }
}