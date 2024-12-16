import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

public class BaseTest {

    @Container
    static LocalStackContainer localStackContainer;

    public static final String TOPIC_ARN = "arn:aws:sns:us-east-1:000000000000:letter-topic";

    static {
        localStackContainer = new LocalStackContainer( DockerImageName.parse("localstack/localstack:3.0"))
                .withCopyFileToContainer(MountableFile.forClasspathResource("init/init-script.sh", 0744), "/etc/localstack/init/ready.d/init-local-stack.sh")
                .withServices(LocalStackContainer.Service.SNS, LocalStackContainer.Service.SQS);
        localStackContainer.start();
    }

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.cloud.aws.credentials.access-key", localStackContainer::getAccessKey);
        registry.add("spring.cloud.aws.credentials.secret-key", localStackContainer::getSecretKey);

        registry.add("spring.cloud.aws.sns.region", localStackContainer::getRegion);
        registry.add("spring.cloud.aws.sns.endpoint", localStackContainer::getEndpoint);
        registry.add("aws.sns.topic.arn", () -> TOPIC_ARN);

        registry.add("spring.cloud.aws.sqs.region", localStackContainer::getRegion);
        registry.add("spring.cloud.aws.sqs.endpoint", localStackContainer::getEndpoint);
    }

}
