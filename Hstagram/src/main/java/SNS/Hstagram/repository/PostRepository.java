package SNS.Hstagram.repository;

import SNS.Hstagram.domain.Post;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostRepository {

    private final EntityManager em;

    // 게시글 저장
    public void save(Post post) {
        em.persist(post);
    }

    // ID로 게시글 조회
    public Post findById(Long id) {
        return em.find(Post.class, id);
    }

    // 사용자 ID로 게시글 조회 (특정 사용자의 게시글 목록)
    public List<Post> findByUserId(Long userId) {
        return em.createQuery("select p from Post p where p.user.id = :userId", Post.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    // 최신 게시글 조회 (시간순 정렬)
    public List<Post> findAllOrderedByCreatedAt() {
        return em.createQuery("select p from Post p order by p.createdAt desc", Post.class)
                .getResultList();
    }

    // 게시글 삭제
    public void delete(Post post) {
        em.remove(post);
    }

}
