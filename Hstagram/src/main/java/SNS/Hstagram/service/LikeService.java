package SNS.Hstagram.service;

import SNS.Hstagram.domain.Comment;
import SNS.Hstagram.domain.Like;
import SNS.Hstagram.domain.Post;
import SNS.Hstagram.domain.User;
import SNS.Hstagram.repository.CommentRepository;
import SNS.Hstagram.repository.LikeRepository;
import SNS.Hstagram.repository.PostRepository;
import SNS.Hstagram.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Transactional
    public void likePost(Long userId, Long postId) {
        validateDuplicateLikeForPost(userId, postId);
        Post post = postRepository.findById(postId);
        if (post == null) {
            throw new IllegalArgumentException("Post not found");
        }

        User user = userRepository.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        Like like = new Like();
        like.setUser(user);
        like.setPost(post);
        likeRepository.save(like);
        post.setLikeCount(post.getLikeCount() + 1);
        postRepository.save(post);
    }

    @Transactional
    public void likeComment(Long userId, Long commentId) {
        validateDuplicateLikeForComment(userId, commentId);
        Comment comment = commentRepository.findById(commentId);
        if (comment == null) {
            throw new IllegalArgumentException("Comment not found");
        }

        User user = userRepository.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        Like like = new Like();
        like.setUser(user);
        like.setComment(comment);
        likeRepository.save(like);
        comment.setLikeCount(comment.getLikeCount() + 1);
        commentRepository.save(comment);
    }


    private void validateDuplicateLikeForPost(Long userId, Long postId) {
        Optional<Like> like = likeRepository.findByUserIdAndPostId(userId, postId);
        if (like.isPresent()) {
            throw new IllegalStateException("Already liked this post");
        }
    }

    private void validateDuplicateLikeForComment(Long userId, Long commentId) {
        Optional<Like> like = likeRepository.findByUserIdAndCommentId(userId, commentId);
        if (like.isPresent()) {
            throw new IllegalStateException("Already liked this comment");
        }
    }
}
