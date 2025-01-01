package SNS.Hstagram.controller;

import SNS.Hstagram.domain.Post;
import SNS.Hstagram.dto.PostDTO;
import SNS.Hstagram.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Tag(name = "Post API", description = "게시글 관련 API")
public class PostController {

    private final PostService postService;

    @PostMapping("/")
    @Operation(summary = "게시글 작성", description = "새로운 게시글을 작성합니다.")
    public ResponseEntity<String> createPost(@RequestParam Long userId,
                                             @RequestParam String content,
                                             @RequestParam(required = false) String imageUrl) {
        postService.addPost(userId, content, imageUrl);
        return ResponseEntity.ok("게시글 작성 완료");
    }

    @PutMapping("/{postId}")
    @Operation(summary = "게시글 수정", description = "게시글의 내용을 수정합니다.")
    public ResponseEntity<String> updatePost(@PathVariable Long postId,
                                             @RequestParam String content,
                                             @RequestParam(required = false) String imageUrl) {
        postService.modifyPost(postId, content, imageUrl);
        return ResponseEntity.ok("게시글 수정 완료");
    }

    @GetMapping("/")
    @Operation(summary = "게시글 조회", description = "모든 게시글을 조회합니다.")
    public ResponseEntity<List<Post>> getAllPosts() {
        return ResponseEntity.ok(postService.findAllPostsList());
    }

    @GetMapping("/{userId}")
    @Operation(summary = "특정 사용자 게시글 조회", description = "특정 사용자가 작성한 게시글을 조회합니다.")
    public ResponseEntity<List<Post>> getPostsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(postService.findUserPostsList(userId));
    }

    @GetMapping("/feed")
    @Operation(summary = "피드 조회", description = "팔로우한 사용자의 게시글을 조회합니다.")
    public ResponseEntity<List<PostDTO>> getFeedList(@RequestParam Long userId) {
        List<PostDTO> feedPosts = postService.findUserFeedList(userId);
        return ResponseEntity.ok(feedPosts);
    }

    @DeleteMapping("/{postId}")
    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다.")
    public ResponseEntity<String> deletePost(@PathVariable Long postId) {
        postService.removePost(postId);
        return ResponseEntity.ok("게시글 삭제 완료");
    }
}
