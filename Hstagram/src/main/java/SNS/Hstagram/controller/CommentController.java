package SNS.Hstagram.controller;

import SNS.Hstagram.domain.Comment;
import SNS.Hstagram.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Tag(name = "Comment API", description = "댓글 관련 API")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{postId}")
    @Operation(summary = "댓글 작성", description = "게시글에 댓글을 작성합니다.")
    public ResponseEntity<String> addComment(@PathVariable Long postId,
                                             @RequestParam String content,
                                             @RequestParam(required = false) Long parentCommentId) {
        commentService.addComment(postId, content, parentCommentId);
        return ResponseEntity.ok("댓글 작성 완료");
    }

    @PutMapping("/{commentId}")
    @Operation(summary = "댓글 수정", description = "댓글의 내용을 수정합니다.")
    public ResponseEntity<String> updateComment(@PathVariable Long commentId,
                                                @RequestParam String content) {
        commentService.modifyComment(commentId, content);
        return ResponseEntity.ok("댓글 수정 완료");
    }

    @GetMapping("/{postId}")
    @Operation(summary = "댓글 조회", description = "특정 게시글에 작성된 댓글을 조회합니다.")
    public ResponseEntity<List<Comment>> getCommentsByPost(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getCommentsByPost(postId));
    }

    @DeleteMapping("/{commentId}")
    @Operation(summary = "댓글 삭제", description = "댓글을 삭제합니다.")
    public ResponseEntity<String> deleteComment(@PathVariable Long commentId) {
        commentService.removeComment(commentId);
        return ResponseEntity.ok("댓글 삭제 완료");
    }
}
