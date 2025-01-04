package SNS.Hstagram.dto;

import SNS.Hstagram.domain.User;
import lombok.Builder;

@Builder
public record UserDTO (
        Long id,
        String name,
        String email
) {
    public static UserDTO from(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
