package SNS.Hstagram.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.ssl.SSLContextBuilder;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.security.KeyStore;

@Configuration
public class ElasticsearchConfig extends ElasticsearchConfiguration {
    @Value("${spring.data.elasticsearch.username}")
    private String username;

    @Value("${spring.data.elasticsearch.password}")
    private String password;

    @Value("${spring.data.elasticsearch.url}")
    private String[] esHost;

    @Value("${spring.data.elasticsearch.ssl.trust-store}")
    private String trustStorePath;

    @Value("${spring.data.elasticsearch.ssl.trust-store-password}")
    private String trustStorePassword;


    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo(esHost)
                .usingSsl(createSSLContext())  // HTTPS 사용 설정
                .withBasicAuth(username, password)
                .build();
    }


    private SSLContext createSSLContext() {
        try {
            String trustStorePath = this.trustStorePath; // truststore 경로
            String trustStorePassword = this.trustStorePassword; // truststore 비밀번호

            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            try (FileInputStream fis = new FileInputStream(trustStorePath)) {
                trustStore.load(fis, trustStorePassword.toCharArray());
            }

            return SSLContextBuilder.create()
                    .loadTrustMaterial(trustStore, null)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create SSLContext", e);
        }
    }


//    @Bean
//    public ElasticsearchClient elasticsearchClient(RestClient restClient) {
//        ObjectMapper objectMapper =
//                new ObjectMapper()
//                        .registerModule(new JavaTimeModule())
//                        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//
//        ElasticsearchTransport transport =
//                new RestClientTransport(restClient, new JacksonJsonpMapper(objectMapper));
//        return new ElasticsearchClient(transport);
//    }

    @Bean
    public ObjectMapper objectMapper() {
        JavaTimeModule module = new JavaTimeModule();
        return new ObjectMapper()
                .registerModule(module)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

}