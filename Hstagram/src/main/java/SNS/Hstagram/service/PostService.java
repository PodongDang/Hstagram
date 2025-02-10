package SNS.Hstagram.service;

import SNS.Hstagram.domain.Post;
import SNS.Hstagram.domain.PostDocument;
import SNS.Hstagram.domain.User;
import SNS.Hstagram.dto.ElasticDTO;
import SNS.Hstagram.dto.PostDTO;
import SNS.Hstagram.dto.RedisDTO;
import SNS.Hstagram.repository.ElasticRepository;
import SNS.Hstagram.repository.FollowRepository;
import SNS.Hstagram.repository.PostRepository;
import SNS.Hstagram.repository.UserRepository;
import SNS.Hstagram.service.kafka.KafkaService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ElasticRepository elasticRepository;
    private final FollowRepository followRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisTemplate<String, String> postTemplate;
    private final S3Uploader s3Uploader;
    //private final SQSService sqsService;
    private final KafkaService kafkaService;

    private static final int FOLLOWER_THRESHOLD = 10; // 팔로워 수 기준

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
            String fullUrl = s3Uploader.getS3ObjectUrl(imageKey);
            post.setImageUrl(fullUrl);
        }

        postRepository.save(post);

        // ✅ Elasticsearch 저장 요청을 Kafka로 보냄
        ElasticDTO elasticDTO = new ElasticDTO();
        elasticDTO.setPostId(post.getId());
        elasticDTO.setUserId(userId);
        elasticDTO.setContent(post.getContent());
        elasticDTO.setImageUrl(post.getImageUrl());
        elasticDTO.setLikeCount(post.getLikeCount());
        elasticDTO.setCommentCount(post.getCommentCount());
        elasticDTO.setPrivate(post.isPrivate());
        elasticDTO.setCreatedAt(OffsetDateTime.now());
        elasticDTO.setUpdatedAt(OffsetDateTime.now());

        kafkaService.sendMessage("elasticsearch-topic", elasticDTO);
        System.out.println("Kafka message sent for Elasticsearch storage");

        // ✅ Redis 저장 요청은 FOLLOWER_THRESHOLD 이하일 때만 Kafka로 보냄
        if (followRepository.countFollowers(user) <= FOLLOWER_THRESHOLD) {
            RedisDTO redisDTO = new RedisDTO();
            redisDTO.setPostId(post.getId());
            redisDTO.setUserId(userId);

            kafkaService.sendMessage("redis-topic", redisDTO);
            System.out.println("Kafka message sent for Redis feed storage");
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

        // Elasticsearch에서 문서 검색 및 업데이트
        PostDocument postDocument = elasticRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Elasticsearch에서 문서를 찾을 수 없습니다."));

        postDocument.setContent(content);
        postDocument.setImageUrl(imageUrl);
        postDocument.setUpdatedAt(OffsetDateTime.now());
        elasticRepository.save(postDocument);

        System.out.println("Elasticsearch document updated for postId: " + postId);
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
    public List<PostDTO> findUserFeedList(Long userId, int page, int size) {
        String feedKey = "feed:" + userId;

        int start = page * size;
        int end = start + size - 1;

        // 1. Redis에서 피드 목록 조회 (postId 목록)
        List<String> postIds = postTemplate.opsForList().range(feedKey, start, end); //paging

        if (postIds == null || postIds.isEmpty()) {
            // Redis에 피드가 없으면 DB 조회
            List<Post> posts = postRepository.findFeedPostsByUserId(userId, PageRequest.of(page, size));
            return posts.stream()
                    .map(PostDTO::from)
                    .collect(Collectors.toList());
        }

        // 2. celeb의 feed 조회하고 feed List에 추가
        List<PostDTO> feedList = new ArrayList<>();

        List<Post> celebPosts = postRepository.findCelebPostsByUserId(userId, FOLLOWER_THRESHOLD, PageRequest.of(page, size));
        feedList.addAll(celebPosts.stream().map(PostDTO::from).collect(Collectors.toList()));

        // 3. postId를 기준으로 Redis에서 PostDTO 조회
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

    // Elastic Search 기능 추가
    public List<PostDocument> searchPostsByKeyword(String keyword) {
        return elasticRepository.findByContentContaining(keyword);
    }

    /**
     * MySQL Full-Text Search를 사용하여 키워드로 게시물을 검색합니다.
     *
     * @param keyword 검색 키워드
     * @return 키워드와 일치하거나 관련된 게시물 목록
     */
    public List<PostDTO> searchPostsByFulltext(String keyword) {
        return postRepository.searchByContent(keyword);
    }
}
