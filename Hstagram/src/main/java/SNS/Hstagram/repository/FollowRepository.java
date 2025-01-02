package SNS.Hstagram.repository;

import SNS.Hstagram.domain.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    @Query("SELECT f FROM Follow f WHERE f.follower.id = :followerId AND f.following.id = :followingId")
    Optional<Follow> findByFollowerAndFollowing(@Param("followerId") Long followerId, @Param("followingId") Long followingId);

    @Query("SELECT f FROM Follow f WHERE f.following.id = :userId")
    List<Follow> findFollowers(@Param("userId") Long userId);

    @Query("SELECT f FROM Follow f WHERE f.follower.id = :userId")
    List<Follow> findFollowing(@Param("userId") Long userId);
}