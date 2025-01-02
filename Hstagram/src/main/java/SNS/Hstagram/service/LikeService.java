package SNS.Hstagram.service;

import SNS.Hstagram.domain.Like;
import SNS.Hstagram.domain.Post;
import SNS.Hstagram.domain.User;
import SNS.Hstagram.domain.Comment;
import SNS.Hstagram.repository.LikeRepository;
import SNS.Hstagram.repository.UserRepository;
import SNS.Hstagram.repository.PostRepository;
import SNS.Hstagram.repository.CommentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    // 게시글 좋아요
    public void likePost(Long userId, Long postId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));

        likeRepository.findByUserIdAndPostId(userId, postId)
                .ifPresent(l -> { throw new IllegalStateException("Already liked this post"); });

        Like like = new Like();
        like.setUser(user);
        like.setPost(post);
        likeRepository.save(like);
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
    }

    // 게시글 좋아요 취소
    public void unlikePost(Long userId, Long postId) {
        Like like = likeRepository.findByUserIdAndPostId(userId, postId)
                .orElseThrow(() -> new EntityNotFoundException("Like not found"));
        likeRepository.delete(like);
    }

    // 댓글 좋아요 취소
    public void unlikeComment(Long userId, Long commentId) {
        Like like = likeRepository.findByUserIdAndCommentId(userId, commentId)
                .orElseThrow(() -> new EntityNotFoundException("Like not found"));
        likeRepository.delete(like);
    }
}