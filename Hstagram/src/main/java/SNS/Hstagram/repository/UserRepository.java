package SNS.Hstagram.repository;

import SNS.Hstagram.domain.Follow;
import SNS.Hstagram.domain.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    @PersistenceContext
    private EntityManager em;

    public void save(User user) { em.persist(user); }

    public User findById(Long id) { return em.find(User.class, id); }

    public List<User> findByName(String name) {
        return em.createQuery("select m from User m where m.name = :name", User.class)
                .setParameter("name", name)
                .getResultList();
    }
    // Optional로 변경
    public Optional<User> findByEmail(String email) {
        try {
            return Optional.ofNullable(
                    em.createQuery("select u from User u where u.email = :email", User.class)
                            .setParameter("email", email)
                            .getSingleResult());
        } catch (Exception e) {
            return Optional.empty();  // 사용자가 없을 경우 Optional.empty() 반환
        }
    }

    public void delete(User user) {
        em.remove(user);
    }
}
