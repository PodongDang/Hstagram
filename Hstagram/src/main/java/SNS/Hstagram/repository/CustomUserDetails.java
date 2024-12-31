package SNS.Hstagram.repository;

import SNS.Hstagram.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    private final User user;  // JPA 엔티티

    public CustomUserDetails(User user) {
        this.user = user;
    }

    // --- 자주 사용하는 헬퍼 ---
    public Long getUserId() {
        return user.getId();
    }

    public String getEmail() {
        return user.getEmail();
    }

    public String getPass() {
        return user.getPassword();
    }
    // ---------------------------------------

    // UserDetails 인터페이스 필수 구현 메서드
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 예: 기본적으로 ROLE_USER 하나만 부여
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getUsername() {
        // Spring Security에서 username으로 사용할 필드
        // 여기서는 email을 username으로 사용
        return user.getEmail();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 추가 로직이 없다면 true
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 추가 로직이 없다면 true
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 추가 로직이 없다면 true
    }

    @Override
    public boolean isEnabled() {
        return true; // 추가 로직이 없다면 true
    }
}
