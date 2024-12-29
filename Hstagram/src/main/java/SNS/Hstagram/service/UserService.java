package SNS.Hstagram.service;

import SNS.Hstagram.domain.User;
import SNS.Hstagram.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    //회원 가입
    public void registerUser(User user) {
        validateDuplicateMember(user);

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);
    }

    private void validateDuplicateMember(User user) {
        //EXCEPTION
        List<User> findMembers = userRepository.findByName(user.getEmail());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("email이 이미 존재하는 회원입니다.");
        }
    }

    // 사용자 정보 조회 (ID 기준)
    public User getUserById(Long id) {
        return userRepository.findById(id);
    }

    // 이메일로 사용자 조회
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // 사용자명으로 사용자 조회
    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByName(username).stream().findFirst();
    }

    // 사용자 정보 수정
    public void updateUser(Long userId, String name, String email) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new EntityNotFoundException("User not found");
        }

        user.setName(name);
        user.setEmail(email);
        userRepository.save(user);
    }

    // 비밀번호 변경
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId);

        if (user == null) {
            throw new EntityNotFoundException("User not found");
        }

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 새 비밀번호 해싱 및 저장
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // 사용자 삭제
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new EntityNotFoundException("User not found");
        }

        userRepository.delete(user);
    }
}