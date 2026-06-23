package hokumei.sys.brackeet.service;

import hokumei.sys.brackeet.config.GmailProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Service
public class GmailEmailSender {

    @Autowired
    private GmailOAuthService oauthService;

    @Autowired
    private GmailProperties props;

    private static final String GMAIL_SEND_URL =
            "https://gmail.googleapis.com/gmail/v1/users/me/messages/send";

    private final RestTemplate restTemplate = new RestTemplate();

    public void send(String to, String subject, String htmlBody) throws Exception {
        String token = oauthService.getValidAccessToken();
        String raw = buildRaw(props.getSenderEmail(), to, subject, htmlBody);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = Map.of("raw", raw);
        restTemplate.postForEntity(GMAIL_SEND_URL, new HttpEntity<>(body, headers), Map.class);
    }

    private String buildRaw(String from, String to, String subject, String htmlBody) {
        String encodedSubject = "=?UTF-8?B?" +
                Base64.getEncoder().encodeToString(subject.getBytes(StandardCharsets.UTF_8)) + "?=";
        String encodedBody = Base64.getMimeEncoder()
                .encodeToString(htmlBody.getBytes(StandardCharsets.UTF_8));

        String message = "From: " + from + "\r\n" +
                "To: " + to + "\r\n" +
                "Subject: " + encodedSubject + "\r\n" +
                "MIME-Version: 1.0\r\n" +
                "Content-Type: text/html; charset=UTF-8\r\n" +
                "Content-Transfer-Encoding: base64\r\n\r\n" +
                encodedBody;

        return Base64.getUrlEncoder().encodeToString(message.getBytes(StandardCharsets.UTF_8));
    }
}
