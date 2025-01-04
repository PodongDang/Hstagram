package SNS.Hstagram.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Configuration
@EnableRedisHttpSession // (스프링 세션 쓰면)
public class RedisConfig {

    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        // GenericJackson2JsonRedisSerializer 등을 사용
        return new GenericJackson2JsonRedisSerializer();
    }

    // 추가로, RedisConnectionFactory, RedisTemplate 설정 등이 필요할 수 있음
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // key Serializer (주로 문자열)
        template.setKeySerializer(new StringRedisSerializer());
        // value Serializer (JSON 직렬화)
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        // 해시 키/값 Serializer
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        template.afterPropertiesSet();
        return template;
    }
}
