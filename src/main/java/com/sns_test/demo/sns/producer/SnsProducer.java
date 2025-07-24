package com.sns_test.demo.sns.producer;

import com.amazon.sns.messaging.lib.core.AmazonSnsTemplate;
import com.amazon.sns.messaging.lib.model.RequestEntry;
import com.amazon.sns.messaging.lib.model.TopicProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.services.sns.SnsClient;

import java.io.Serializable;
import java.net.URI;
import java.util.Map;
import java.util.UUID;

import software.amazon.awssdk.regions.Region;

@Service
public class SnsProducer {

    private final SnsClient amazonSNS;
    private final TopicProperty topicProperty;
    final AmazonSnsTemplate<MyMessage> snsTemplate;

    // To run this code, you need to have LocalStack running with SNS service and configure the environment variables for AWS credentials.
    // export AWS_ACCESS_KEY_ID=test
    // export AWS_SECRET_ACCESS_KEY=test
    // export AWS_REGION=us-east-1

    public SnsProducer() {
        this.amazonSNS = SnsClient.builder()
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .region(Region.US_EAST_1)
                .endpointOverride(URI.create("http://localhost:4566"))
                .build();

        topicProperty = TopicProperty.builder()
                .fifo(true)
                .linger(10)
                .maxBatchSize(10)
                .maximumPoolSize(5120)
                .topicArn("arn:aws:sns:us-east-1:000000000000:test-topic.fifo")
                .build();

        snsTemplate = new AmazonSnsTemplate<>(amazonSNS, topicProperty, new ObjectMapper());
    }

    public void sendMessage() {
        final RequestEntry<MyMessage> requestEntry = RequestEntry.<MyMessage>builder()
                .withValue(new MyMessage())
                .withMessageHeaders(Map.of())
                .withGroupId(UUID.randomUUID().toString())
                .withDeduplicationId(UUID.randomUUID().toString())
                .build();

        snsTemplate.send(requestEntry).addCallback(result -> {
            System.out.println("Message sent successfully: " + result);
        }, ex -> {
            System.err.println("Failed to send message: " + ex.getMessage());
        });
    }

    @Getter
    @Setter
    public static class MyMessage implements Serializable {
        private UUID id = UUID.randomUUID();
        private String message;
        private String timestamp;
        private String sender;
        private String receiver;
        private String status;
        private String type;
        private String content;
        private byte[] payload;

        public MyMessage() {
            this.message = "Hello, this is a test message!";
            this.timestamp = String.valueOf(System.currentTimeMillis());
            this.sender = "test-sender";
            this.receiver = "test-receiver";
            this.status = "NEW";
            this.type = "text";
            this.content = "This is the content of the message.";

            int sizeInKB = 3;
            this.payload = new byte[sizeInKB * 1024];
        }
    }

}
