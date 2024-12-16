import com.christmas.letter.sender.LetterSenderApplication;
import com.christmas.letter.sender.model.Letter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import net.bytebuddy.utility.RandomString;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ExtendWith(OutputCaptureExtension.class)
@SpringBootTest(classes = LetterSenderApplication.class)
public class LetterControllerIntegrationTest extends BaseTest {

    @Autowired
    private MockMvc mockMvc;

    final String userCreationApiPath = "/letter/";

    static Stream<Object[]> invalidArgumentsProvider() {
        return Stream.of(
                new Object[]{"", RandomString.make(), RandomString.make(), List.of(RandomString.make()), RandomString.make(), "{\"email\":\"must not be blank\"}"},
                new Object[]{RandomString.make(), RandomString.make(), RandomString.make(), List.of(RandomString.make()), RandomString.make(), "{\"email\":\"must be a well-formed email address\"}"},
                new Object[]{RandomString.make() + "@test.com", RandomString.make(), "", List.of(RandomString.make()), RandomString.make(), "{\"body\":\"must not be blank\"}"},
                new Object[]{RandomString.make() + "@test.com", RandomString.make(), RandomString.make(), null, RandomString.make(), "{\"wishes\":\"must not be empty\"}"},
                new Object[]{RandomString.make() + "@test.com", RandomString.make(), RandomString.make(), List.of(RandomString.make()), null, "{\"location\":\"must not be blank\"}"}
        );
    }

    @Test
    @SneakyThrows
    void testSendLetterWithSuccess(CapturedOutput output) {
        final String email= RandomString.make() + "@test.com";
        final String name = RandomString.make();
        final String body = RandomString.make();
        final List<String> wishes = List.of(RandomString.make());
        final String location = RandomString.make();
        Letter letter = new Letter(email, name, body, wishes, location);

        mockMvc.perform(post(userCreationApiPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(letter)))
                .andExpect(status().isCreated());

        final String expectedPublisherLog = String.format("Successfully published message to topic ARN: %s", BaseTest.TOPIC_ARN);
        Awaitility.await().atMost(1, TimeUnit.SECONDS).until(() -> output.getAll().contains(expectedPublisherLog));

    }

    @ParameterizedTest
    @MethodSource("invalidArgumentsProvider")
    public void testSendLetterWithInvalidArguments(String email, String name, String body, List<String> wishes, String location, String expectedMessage) throws Exception {
        Letter letter = new Letter(email, name, body, wishes, location);

        mockMvc.perform(post(userCreationApiPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(letter)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(expectedMessage));
    }
}
