package com.christmas.letter.sender.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "aws.sns.topic")
public class AwsSnsTopicProperties {

    private String arn;
}

