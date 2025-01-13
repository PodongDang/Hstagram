package SNS.Hstagram.service;

import io.awspring.cloud.sqs.operations.SendResult;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

@Service
@RequiredArgsConstructor
public class SQSService {

    private final SqsTemplate template;

    @Value("${aws.sqs.queue.url}") // Queue URL 사용
    private String queueUrl;

    public SendResult<String> sendMessage(String queueName, String message) {
        try {
            System.out.println("Sender: " + message);
            return template.send(to -> to
                    .queue(queueName) // Standard Queue에서는 Queue Name 사용 가능
                    .payload(message)); // MessageGroupId와 MessageDeduplicationId 제거
        } catch (Exception e) {
            System.err.println("Failed to send message to SQS: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

}
