package SNS.Hstagram.service.SQS;

import SNS.Hstagram.domain.Follow;
import SNS.Hstagram.dto.RedisDTO;
import SNS.Hstagram.repository.FollowRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "sqs.enabled", havingValue = "true", matchIfMissing = false)
public class SqsMessageListener {  // 기존 MessageListener → SqsMessageListener 로 변경

    private final FollowRepository followRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    //@SqsListener("${aws.sqs.queue.url}") // 주석 처리 가능
    public void messageListener(String message) {
        try {
            // 메시지 파싱
            RedisDTO postMessage = objectMapper.readValue(message, RedisDTO.class);
            Long postId = postMessage.getPostId();
            Long userId = postMessage.getUserId();

            // 데이터 처리
            List<Follow> followers = followRepository.findFollowers(userId);
            for (Follow follow : followers) {
                String feedKey = "feed:" + follow.getFollower().getId();
                redisTemplate.opsForList().leftPush(feedKey, postId);
            }

            System.out.println("Successfully processed postId: " + postId + ", userId: " + userId);
        } catch (Exception e) {
            System.err.println("Failed to process message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
