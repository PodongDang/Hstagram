package SNS.Hstagram.config;

import SNS.Hstagram.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    @Autowired
    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .userDetailsService(userDetailsService)
                .csrf(csrf -> csrf.disable()) // CSRF 비활성화 (필요시 활성화)
                .formLogin(form -> form.disable())
                .formLogin(form -> form
                        .loginPage("/login")              // 커스텀 로그인 페이지
                        .loginProcessingUrl("/login")     // 로그인 폼의 POST action
                        .defaultSuccessUrl("/")           // 로그인 성공 시 이동할 URL
                        .permitAll())
                .httpBasic(httpBasic -> httpBasic.disable())  // HTTP Basic 인증 비활성화
                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers(
//                                // -- Swagger UI v2
//                                "/v2/api-docs",
//                                "/swagger-resources",
//                                "/swagger-resources/**",
//                                "/configuration/ui",
//                                "/configuration/security",
//                                "/swagger-ui.html",
//                                "/webjars/**",
//                                // -- Swagger UI v3 (OpenAPI)
//                                "/v3/api-docs/**",
//                                "/swagger-ui/**",
//                                // -- login, singup
//                                "/api/users/register",
//                                "/api/users/login",
//                                "/api/follow/**",
//                                "/login"
//                        ).permitAll()  // 인증 없이 접근 가능
                        .anyRequest().permitAll()  // 그 외 요청은 인증 필요
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
                );

        return http.build();
    }




}

