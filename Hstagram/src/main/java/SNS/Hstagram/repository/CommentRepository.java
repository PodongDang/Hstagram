package SNS.Hstagram.repository;

import SNS.Hstagram.domain.Comment;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommentRepository {

    @PersistenceContext
    private EntityManager em;

    // 댓글 저장
    public void save(Comment comment) {
        em.persist(comment);
    }

    // ID로 댓글 조회
    public Comment findById(Long id) {
        return em.find(Comment.class, id);
    }

    // 특정 게시글에 달린 댓글 목록 조회
    public List<Comment> findByPostId(Long postId) {
        return em.createQuery("select c from Comment c where c.post.id = :postId order by c.createdAt asc", Comment.class)
                .setParameter("postId", postId)
                .getResultList();
    }

    // 특정 댓글의 대댓글 조회
    public List<Comment> findReplies(Long parentCommentId) {
        return em.createQuery("select c from Comment c where c.parentComment.id = :parentId", Comment.class)
                .setParameter("parentId", parentCommentId)
                .getResultList();
    }

    // 댓글 삭제
    public void delete(Comment comment) {
        em.remove(comment);
    }
}
