package SNS.Hstagram.service;

import SNS.Hstagram.domain.User;
import SNS.Hstagram.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class UserServiceTest {

    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    EntityManager em;

    @Test
    public void 회원가입_테스트() throws Exception {
        //given
        User user = new User();
        user.setName("황연준");
        user.setEmail("amongzamong@gmail.com");
        user.setPassword("password123");

        //when
        userService.addUser(user);
        User savedUser = userService.findUserById(user.getId());

        //then
        assertEquals("유저 저장 완료", savedUser.getId(), user.getId());
    }

}