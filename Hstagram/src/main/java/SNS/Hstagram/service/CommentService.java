package SNS.Hstagram.service;

import SNS.Hstagram.domain.Comment;
import SNS.Hstagram.domain.Post;
import SNS.Hstagram.repository.CommentRepository;
import SNS.Hstagram.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    // 댓글 작성
    public void addComment(Long postId, String reply, Long parentCommentId) {
        Post post = postRepository.findById(postId);
        if (post == null) {
            throw new EntityNotFoundException("Post not found");
        }

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setReply(reply);

        if (parentCommentId != null) {
            Comment parentComment = commentRepository.findById(parentCommentId);
            if (parentComment == null) {
                throw new EntityNotFoundException("Parent comment not found");
            }
            comment.setParentComment(parentComment);
        }
        commentRepository.save(comment);
    }

    // 댓글 업데이트 (내용 수정)
    public void updateComment(Long commentId, String reply) {
        Comment comment = commentRepository.findById(commentId);
        if (comment == null) {
            throw new EntityNotFoundException("댓글을 찾을 수 없습니다.");
        }

        comment.setReply(reply);
        comment.setUpdatedAt(java.time.LocalDateTime.now());
        commentRepository.save(comment);
    }

    // 게시글의 모든 댓글 조회
    public List<Comment> getCommentsByPost(Long postId) {
        return commentRepository.findByPostId(postId);
    }

    // 댓글 삭제
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId);
        if (comment != null) {
            commentRepository.delete(comment);
        } else {
            throw new EntityNotFoundException("Comment not found");
        }
    }

    // 대댓글 조회
    public List<Comment> getReplies(Long parentCommentId) {
        return commentRepository.findReplies(parentCommentId);
    }
}
