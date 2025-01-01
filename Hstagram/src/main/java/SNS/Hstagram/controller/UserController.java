package SNS.Hstagram.controller;

import SNS.Hstagram.domain.User;
import SNS.Hstagram.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User API", description = "사용자 관련 API")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    public ResponseEntity<String> register(@RequestBody User user) {
        userService.addUser(user);
        return ResponseEntity.ok("회원가입 완료");
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인합니다.")
    public ResponseEntity<String> login(@RequestBody Map<String, String> loginRequest, HttpSession session) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        userService.login(email, password, session);
        return ResponseEntity.ok("로그인 성공");
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "현재 사용자의 세션을 만료시킵니다.")
    public ResponseEntity<String> logout(HttpSession session) {
        userService.logout(session);
        return ResponseEntity.ok("로그아웃 완료");
    }

    @GetMapping("/{id}")
    @Operation(summary = "사용자 조회", description = "ID를 기준으로 사용자 정보를 조회합니다.")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.findUserById(id);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "사용자 삭제", description = "ID를 기준으로 사용자 정보를 삭제합니다.")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.removeUser(id);
        return ResponseEntity.ok("사용자 삭제 완료");
    }
}
