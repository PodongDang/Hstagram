package SNS.Hstagram.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// 인증 실패 예외
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class AuthException {

    public static class FailAuthenticationMemberException extends RuntimeException {
        public FailAuthenticationMemberException() {
            super("인증되지 않은 사용자입니다.");
        }
    }
}
