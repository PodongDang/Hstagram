package SNS.Hstagram.repository;

import SNS.Hstagram.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 특정 게시글에 달린 댓글 목록 조회
    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId ORDER BY c.createdAt ASC")
    List<Comment> findByPostId(@Param("postId") Long postId);

    // 특정 댓글의 대댓글 조회
    @Query("SELECT c FROM Comment c WHERE c.parentComment.id = :parentId")
    List<Comment> findReplies(@Param("parentId") Long parentCommentId);
}
