//package SNS.Hstagram.interceptor;
//
//import SNS.Hstagram.exception.AuthException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.HandlerInterceptor;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.beans.factory.annotation.Value;
//import java.util.Base64;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class AuthInterceptor implements HandlerInterceptor {
//
//    private static final String SESSION_KEY = "JSESSIONID";
//
//    private static final String REDIS_SESSION_KEY = ":sessions:";
//
//
//    @Value("${spring.session.redis.namespace}")
//    private String namespace;
//
//    private final StringRedisTemplate redisTemplate;
//
//    @Override
//    public boolean preHandle(final HttpServletRequest request,
//                             final HttpServletResponse response,
//                             final Object handler) throws Exception {
//
//        // 로그인 및 회원가입 요청은 통과
//        if (request.getRequestURI().contains("/api/users/login") ||
//                request.getRequestURI().contains("/api/users/register")) {
//            return true;
//        }
//        // 1. 쿠키에서 세션 ID 추출
//        final String sessionIdByCookie = getSessionIdByCookie(request);
//
//        // 2. 세션 ID 디코딩
//        if (sessionIdByCookie == null) {
//            log.warn("Session Cookie not found");
//            throw new AuthException.FailAuthenticationMemberException();
//        }
//        final String decodedSessionId = new String(Base64.getDecoder().decode(sessionIdByCookie.getBytes()));
//
//        // 3. Redis에서 세션 확인
//        if (!redisTemplate.hasKey(namespace + REDIS_SESSION_KEY + decodedSessionId)) {
//            log.warn("Session Cookie exists, but Session in Redis not found");
//            throw new AuthException.FailAuthenticationMemberException();
//        }
//
//        log.info("Valid Session: {}", decodedSessionId);
//        return true;
//    }
//
//    // 요청에서 세션 쿠키 추출
//    private String getSessionIdByCookie(HttpServletRequest request) {
//        if (request.getCookies() != null) {
//            for (var cookie : request.getCookies()) {
//                if (SESSION_KEY.equals(cookie.getName())) {
//                    return cookie.getValue();
//                }
//            }
//        }
//        return null;
//    }
//}
