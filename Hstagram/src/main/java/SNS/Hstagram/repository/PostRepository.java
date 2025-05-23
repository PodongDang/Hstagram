package SNS.Hstagram.repository;

import SNS.Hstagram.domain.Post;
import SNS.Hstagram.dto.PostDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    // 사용자 ID로 게시글 조회 (특정 사용자의 게시글 목록)
    @Query("SELECT p FROM Post p JOIN FETCH p.user u WHERE u.id = :userId")
    List<PostDTO> findAllByUserId(@Param("userId") Long userId);

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
    List<Post> findFeedPostsByUserId(@Param("userId") Long userId, Pageable pageable);

    //full-text index
    @Query(value = "SELECT p.id AS id, p.content AS content, p.image_url AS imageUrl " +
            "FROM post p WHERE MATCH(p.content) AGAINST(:keyword IN NATURAL LANGUAGE MODE)", nativeQuery = true)
    List<PostDTO> searchByContent(@Param("keyword") String keyword);

    @Query("""
    SELECT DISTINCT p 
    FROM Post p 
    JOIN FETCH p.user u 
    WHERE u.id IN (
        SELECT f.following.id 
        FROM Follow f 
        WHERE f.follower.id = :userId
    )
    AND (SELECT COUNT(f) FROM Follow f WHERE f.following.id = u.id) > :followThreshold
    ORDER BY p.createdAt DESC
    """)
    List<Post> findCelebPostsByUserId(@Param("userId") Long userId, @Param("followThreshold") int followThreshold, Pageable pageable);


}
