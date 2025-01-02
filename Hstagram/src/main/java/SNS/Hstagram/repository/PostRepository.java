package SNS.Hstagram.repository;

import SNS.Hstagram.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    // 사용자 ID로 게시글 조회 (특정 사용자의 게시글 목록)
    @Query("SELECT p FROM Post p WHERE p.user.id = :userId")
    List<Post> findAllByUserId(@Param("userId") Long userId);

    // 최신 게시글 조회 (시간순 정렬)
    @Query("SELECT p FROM Post p ORDER BY p.createdAt DESC")
    List<Post> findAllOrderedByCreatedAt();

    // 피드 조회 - fetch join 사용
    @Query(
            "SELECT p FROM Post p " +
                    "JOIN FETCH p.user u " +
                    "WHERE u.id IN (" +
                    "  SELECT f.following.id FROM Follow f WHERE f.follower.id = :userId" +
                    ") ORDER BY p.createdAt DESC"
    )
    List<Post> findFeedPostsByUserId(@Param("userId") Long userId);
}
