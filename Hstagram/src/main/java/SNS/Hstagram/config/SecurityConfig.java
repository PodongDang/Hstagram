package SNS.Hstagram.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // CSRF 비활성화 (필요시 활성화)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                // -- Swagger UI v2
                                "/v2/api-docs",
                                "/swagger-resources",
                                "/swagger-resources/**",
                                "/configuration/ui",
                                "/configuration/security",
                                "/swagger-ui.html",
                                "/webjars/**",
                                // -- Swagger UI v3 (OpenAPI)
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                // -- login, singup
                                "/api/users/register",
                                "/api/users/login"
                        ).permitAll()  // 인증 없이 접근 가능
                        .anyRequest().authenticated()  // 그 외 요청은 인증 필요
                )
                .sessionManagement(sess -> sess
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)  // 세션 사용
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(401);
                            response.getWriter().write("Unauthorized: " + authException.getMessage());
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(403);
                            response.getWriter().write("Forbidden: " + accessDeniedException.getMessage());
                        })
                )
                .formLogin(formLogin -> formLogin.disable())  // 폼 로그인 비활성화
                .httpBasic(httpBasic -> httpBasic.disable());  // HTTP Basic 인증 비활성화

        return http.build();
    }




}

