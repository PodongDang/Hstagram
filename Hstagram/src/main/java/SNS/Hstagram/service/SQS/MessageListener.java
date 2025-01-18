package SNS.Hstagram.service.SQS;

import SNS.Hstagram.domain.Follow;
import SNS.Hstagram.dto.SqsDTO;
import SNS.Hstagram.repository.FollowRepository;
import io.awspring.cloud.sqs.annotation.SqsListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MessageListener {

    private final FollowRepository followRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @SqsListener("${aws.sqs.queue.url}")
    public void messageListener(String message) {
        try {
            // 메시지 파싱
            SqsDTO postMessage = objectMapper.readValue(message, SqsDTO.class);
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
