package SNS.Hstagram.service;

import SNS.Hstagram.domain.Follow;
import SNS.Hstagram.domain.Post;
import SNS.Hstagram.domain.User;
import SNS.Hstagram.dto.PostDTO;
import SNS.Hstagram.repository.FollowRepository;
import SNS.Hstagram.repository.PostRepository;
import SNS.Hstagram.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisTemplate<String, String> postTemplate;
    private final S3Uploader s3Uploader;

    // 게시글 작성
    /**
     * (1) Presigned URL 발급 메서드
     *  - originalFilename, contentType을 받아서 S3 Uploader에게 넘김
     *  - 클라이언트가 이 URL로 PUT 업로드 할 예정
     */
    public String createPresignedUrl(String originalFilename, String contentType) {
        return s3Uploader.createPresignedUrl(originalFilename, contentType);
    }

    /**
     * (2) 최종 게시글 등록
     *  - S3에 업로드가 끝났다고 가정하고, 그 Key를 받아서 저장
     *  - imageKey가 null이 아닐 경우, 실제 S3 URL을 만들어서 post.setImageUrl()에 저장
     */
    public void addPost(Long userId, String content, String imageKey) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Post post = new Post();
        post.setUser(user);
        post.setContent(content);

        if (imageKey != null && !imageKey.isBlank()) {
            // 예: "https://bucket-name.s3.amazonaws.com/{imageKey}"
            String fullUrl = s3Uploader.getS3ObjectUrl(imageKey);
            post.setImageUrl(fullUrl);
        }

        postRepository.save(post);

        // Post -> PostDTO로 변환
        PostDTO postDTO = PostDTO.from(post);

        // 1. 게시글을 Redis에 캐싱 (key: post:{postId})
        String postKey = "post:" + post.getId();
        redisTemplate.opsForValue().set(postKey, postDTO, Duration.ofDays(30)); //redis에는 30일동안 유지

        // 2. 팔로워 피드에 postId 푸시
        List<Follow> followers = followRepository.findFollowers(userId);
        for (Follow follow : followers) {
            String feedKey = "feed:" + follow.getFollower().getId();
            redisTemplate.opsForList().leftPush(feedKey, post.getId());
        }
    }

    // 게시글 업데이트 (내용 및 이미지 수정)
    public void modifyPost(Long postId, String content, String imageUrl) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        post.setContent(content);
        post.setImageUrl(imageUrl);
        post.setUpdatedAt(java.time.LocalDateTime.now());
        postRepository.save(post);
    }

    // 특정 사용자 게시글 조회
    public List<PostDTO> findUserPostsList(Long userId) {
        return postRepository.findAllByUserId(userId);
    }

    // 모든 게시글 조회 (최신순)
    public List<Post> findAllPostsList() {
        return postRepository.findAllOrderedByCreatedAt();
    }

    // 피드 조회
    public List<PostDTO> findUserFeedList(Long userId) {
        String feedKey = "feed:" + userId;

        // 1. Redis에서 피드 목록 조회 (postId 목록)
        List<String> postIds = postTemplate.opsForList().range(feedKey, 0, 99); //100개까지만 유지

        if (postIds == null || postIds.isEmpty()) {
            // Redis에 피드가 없으면 DB 조회
            List<Post> posts = postRepository.findFeedPostsByUserId(userId);
            return posts.stream()
                    .map(PostDTO::from)
                    .collect(Collectors.toList());
        }

        // 2. postId를 기준으로 Redis에서 PostDTO 조회
        List<PostDTO> feedList = new ArrayList<>();
        for (String postId : postIds) {
            String postKey = "post:" + postId;
            PostDTO postDTO = (PostDTO) redisTemplate.opsForValue().get(postKey);

            if (postDTO == null) {
                // Redis에 PostDTO가 없으면 DB에서 조회 후 캐싱
                Post post = postRepository.findById(Long.parseLong(postId))
                        .orElseThrow(() -> new EntityNotFoundException("Post not found"));

                postDTO = PostDTO.from(post);
                redisTemplate.opsForValue().set(postKey, postDTO, Duration.ofDays(30));
            }
            feedList.add(postDTO);
        }

        return feedList;
    }

    // 게시글 삭제
    public void removePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));
        postRepository.delete(post);
    }
}
