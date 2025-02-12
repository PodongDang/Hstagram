package SNS.Hstagram.controller;

import SNS.Hstagram.domain.Post;
import SNS.Hstagram.domain.PostDocument;
import SNS.Hstagram.dto.PostDTO;
import SNS.Hstagram.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Tag(name = "Post API", description = "게시글 관련 API")
public class PostController {

    private final PostService postService;

    /**
     * (1) Presigned URL 발급: 클라이언트에서 이미지 업로드를 원할 경우,
     *    파일명(or contentType 등)을 파라미터로 받아 Presigned URL을 생성해 반환
     */
    @GetMapping("/presigned")
    @Operation(summary = "Presigned URL 발급", description = "S3에 이미지 업로드를 위한 Presigned URL을 발급합니다.")
    public ResponseEntity<Map<String, String>> getPresignedUrl(
            @RequestParam String originalFilename,
            @RequestParam String contentType
    ) {
        String presignedUrl = postService.createPresignedUrl(originalFilename, contentType);

        Map<String, String> result = new HashMap<>();
        result.put("presignedUrl", presignedUrl);
        // S3에 업로드할 때 사용해야 할 key(파일명)도 넘겨주면 좋음
        result.put("fileKey", originalFilename);

        return ResponseEntity.ok(result);
    }

    /**
     * (2) 게시글 등록:
     *    - S3에 이미지를 '이미' 업로드한 뒤, 서버로 최종 게시글 정보(userId, content, imageKey 등)를 보낸다고 가정
     *    - imageKey는 프론트가 Presigned URL로 PUT 업로드할 때 사용했던 Key(파일명)입니다.
     */
    @PostMapping("/")
    @Operation(summary = "게시글 작성", description = "이미지를 S3에 업로드한 후, 게시글 등록을 완료합니다.")
    public ResponseEntity<String> createPostWithImage(
            @RequestParam Long userId,
            @RequestParam String content,
            @RequestParam(required = false) String imageKey
    ) {
        postService.addPost(userId, content, imageKey);
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
    public ResponseEntity<List<PostDTO>> getPostsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(postService.findUserPostsList(userId));
    }

    @GetMapping("/feed")
    @Operation(summary = "피드 조회", description = "팔로우한 사용자의 게시글을 조회합니다.")
    public ResponseEntity<List<PostDTO>> getFeedList(@RequestParam Long userId) {
        List<PostDTO> feedPosts = postService.findUserFeedList(userId, 1, 10);
        return ResponseEntity.ok(feedPosts);
    }

    @GetMapping("/search")
    @Operation(summary = "게시물 검색", description = "keyword에 근접한 게시글을 조회합니다.")
    public List<PostDocument> searchPosts(@RequestParam String keyword) {
        return postService.searchPostsByKeyword(keyword);
    }

    /**
     * MySQL Full-Text Search 기반 게시물 검색 API
     *
     * @param keyword 검색 키워드
     * @return 키워드에 근접한 게시물 목록
     */
    @GetMapping("/fulltext")
    @Operation(summary = "MySQL 게시물 검색", description = "MySQL Full-Text Search를 사용하여 keyword에 근접한 게시글을 조회합니다.")
    public List<PostDTO> searchPostsByFulltext(@RequestParam String keyword) {
        return postService.searchPostsByFulltext(keyword);
    }

    @DeleteMapping("/{postId}")
    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다.")
    public ResponseEntity<String> deletePost(@PathVariable Long postId) {
        postService.removePost(postId);
        return ResponseEntity.ok("게시글 삭제 완료");
    }

}
