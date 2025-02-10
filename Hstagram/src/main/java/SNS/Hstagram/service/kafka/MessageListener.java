package SNS.Hstagram.service.kafka;

import SNS.Hstagram.domain.Follow;
import SNS.Hstagram.domain.PostDocument;
import SNS.Hstagram.dto.ElasticDTO;
import SNS.Hstagram.dto.RedisDTO;
import SNS.Hstagram.repository.ElasticRepository;
import SNS.Hstagram.repository.FollowRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MessageListener {

    private final FollowRepository followRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ElasticRepository elasticRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "redis-topic", groupId = "group_1")
    public void handleRedisMessage(String message) {
        try {
            RedisDTO redisDTO = objectMapper.readValue(message, RedisDTO.class);
            Long postId = redisDTO.getPostId();
            Long userId = redisDTO.getUserId();

            List<Follow> followers = followRepository.findFollowers(userId);
            for (Follow follow : followers) {
                String feedKey = "feed:" + follow.getFollower().getId();
                redisTemplate.opsForList().leftPush(feedKey, postId);
            }

            System.out.println("Successfully processed postId: " + postId + " for Redis storage");

        } catch (Exception e) {
            System.err.println("Failed to process Redis message: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @KafkaListener(topics = "elasticsearch-topic", groupId = "group_2")
    public void handleElasticsearchMessage(String message) {
        try {
            ElasticDTO elasticDTO = objectMapper.readValue(message, ElasticDTO.class);

            PostDocument postDocument = new PostDocument();
            postDocument.setId(elasticDTO.getPostId());
            postDocument.setUserId(elasticDTO.getUserId());
            postDocument.setContent(elasticDTO.getContent());
            postDocument.setImageUrl(elasticDTO.getImageUrl());
            postDocument.setLikeCount(elasticDTO.getLikeCount());
            postDocument.setCommentCount(elasticDTO.getCommentCount());
            postDocument.setPrivate(elasticDTO.isPrivate());
            postDocument.setCreatedAt(elasticDTO.getCreatedAt());
            postDocument.setUpdatedAt(elasticDTO.getUpdatedAt());

            elasticRepository.save(postDocument);
            System.out.println("Successfully stored postId: " + elasticDTO.getPostId() + " in Elasticsearch");

        } catch (Exception e) {
            System.err.println("Failed to process Elasticsearch message: " + e.getMessage());
            e.printStackTrace();
        }
    }

}

