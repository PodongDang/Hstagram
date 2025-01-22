package SNS.Hstagram.service;

import SNS.Hstagram.domain.Like;
import SNS.Hstagram.domain.Post;
import SNS.Hstagram.domain.User;
import SNS.Hstagram.domain.Comment;
import SNS.Hstagram.repository.LikeRepository;
import SNS.Hstagram.repository.UserRepository;
import SNS.Hstagram.repository.PostRepository;
import SNS.Hstagram.repository.CommentRepository;
import SNS.Hstagram.repository.FollowRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final FollowRepository followRepository;

    // In-memory Batch Store
    private final ConcurrentHashMap<Long, AtomicInteger> batchLikeCounts = new ConcurrentHashMap<>();
    private static final int BATCH_SIZE = 10; // Batch 기준 크기
    private static final int FOLLOWER_THRESHOLD = 10; // 팔로워 수 기준

    // 게시글 좋아요
    public void likePost(Long userId, Long postId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));

        // 이미 좋아요 여부 확인
        likeRepository.findByUserIdAndPostId(userId, postId)
                .ifPresent(l -> { throw new IllegalStateException("Already liked this post"); });

        // Like 객체 저장
        Like like = new Like();
        like.setUser(user);
        like.setPost(post);
        likeRepository.save(like);

        // 팔로워 수에 따라 처리 분기
        long followerCount = followRepository.countFollowers(post.getUser());
        if (followerCount > FOLLOWER_THRESHOLD) {
            // Batch Write
            batchIncrementLikeCount(post);
        } else {
            // 즉시 DB 업데이트
            incrementLikeCountImmediately(post);
        }
    }

    // 즉시 DB 업데이트
    private void incrementLikeCountImmediately(Post post) {
        post.setLikeCount(post.getLikeCount() + 1);
        postRepository.save(post);
    }

    // Batch Write 방식으로 처리
    private void batchIncrementLikeCount(Post post) {
        batchLikeCounts.computeIfAbsent(post.getId(), key -> new AtomicInteger(0)).incrementAndGet();

        // Batch 크기 초과 시 DB로 Write
        if (batchLikeCounts.get(post.getId()).get() >= BATCH_SIZE) {
            flushBatchLikeCount(post);
        }
    }

    // Batch를 DB로 Write
    private synchronized void flushBatchLikeCount(Post post) {
        AtomicInteger count = batchLikeCounts.get(post.getId());
        if (count == null || count.get() == 0) return;

        // DB 업데이트
        post.setLikeCount(post.getLikeCount() + count.get());
        postRepository.save(post);

        // In-Memory 데이터 초기화
        count.set(0);
    }

    // 댓글 좋아요
    public void likeComment(Long userId, Long commentId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));

        likeRepository.findByUserIdAndCommentId(userId, commentId)
                .ifPresent(l -> { throw new IllegalStateException("Already liked this comment"); });

        Like like = new Like();
        like.setUser(user);
        like.setComment(comment);
        likeRepository.save(like);

        // 댓글 좋아요는 즉시 업데이트
        comment.setLikeCount(comment.getLikeCount() + 1);
        commentRepository.save(comment);
    }
}
