package SNS.Hstagram.repository;

import SNS.Hstagram.domain.Follow;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FollowRepository {

    @PersistenceContext
    private EntityManager em;

    // 팔로우 저장
    public void save(Follow follow) {
        em.persist(follow);
    }

    // 특정 팔로우 관계 조회 (follower → following)
    public Follow findByFollowerAndFollowing(Long followerId, Long followingId) {
        try {
            return em.createQuery(
                            "select f from Follow f where f.follower.id = :followerId and f.following.id = :followingId", Follow.class)
                    .setParameter("followerId", followerId)
                    .setParameter("followingId", followingId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;  // 결과가 없을 경우 null 반환
        }
    }


    // 사용자의 팔로워 목록 조회
    public List<Follow> findFollowers(Long userId) {
        return em.createQuery("select f from Follow f where f.following.id = :userId", Follow.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    // 사용자가 팔로우하는 목록 조회
    public List<Follow> findFollowing(Long userId) {
        return em.createQuery("select f from Follow f where f.follower.id = :userId", Follow.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    // 팔로우 관계 삭제
    public void delete(Follow follow) {
        em.remove(follow);
    }
}
