package SNS.Hstagram.service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper; // ✅ JSON 변환을 위한 ObjectMapper

    public void sendMessage(String topic, Object dto) {
        try {
            String message = objectMapper.writeValueAsString(dto); // ✅ DTO를 JSON 문자열로 변환
            System.out.println("Sending message to " + topic + ": " + message);
            kafkaTemplate.send(topic, message);
        } catch (Exception e) {
            System.err.println("Failed to send message to Kafka: " + e.getMessage());
            e.printStackTrace();
        }
    }
}


