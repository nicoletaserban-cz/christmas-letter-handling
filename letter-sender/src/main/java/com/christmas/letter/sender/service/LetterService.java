package com.christmas.letter.sender.service;

import com.christmas.letter.sender.configuration.AwsSnsTopicProperties;
import com.christmas.letter.sender.model.Letter;
import io.awspring.cloud.sns.core.SnsTemplate;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(AwsSnsTopicProperties.class)
public class LetterService {

    private final SnsTemplate snsTemplate;
    private final AwsSnsTopicProperties awsSnsTopicProperties;

    public void send(@NonNull final Letter letter) {

        final String topicArn = awsSnsTopicProperties.getArn();
        snsTemplate.convertAndSend(topicArn, letter);
        log.info("Successfully published message to topic ARN: {}", topicArn);
    }

}
