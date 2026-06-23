package hokumei.sys.brackeet.service.impl;

import hokumei.sys.brackeet.config.GmailProperties;
import hokumei.sys.brackeet.service.GmailOAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class GmailOAuthServiceImpl implements GmailOAuthService {

    private static final String TOKEN_ENDPOINT = "https://oauth2.googleapis.com/token";

    @Autowired
    private GmailProperties props;

    private final RestTemplate restTemplate = new RestTemplate();

    private volatile String accessToken;
    private volatile long tokenExpiry;

    @Override
    public String getAuthorizationUrl() {
        throw new UnsupportedOperationException("Not needed — use pre-configured refresh token");
    }

    @Override
    public void handleCallback(String code) {
        throw new UnsupportedOperationException("Not needed — use pre-configured refresh token");
    }

    @Override
    public String getValidAccessToken() throws Exception {
        if (accessToken != null && System.currentTimeMillis() < tokenExpiry) {
            return accessToken;
        }
        String refreshToken = props.getRefreshToken();
        if (refreshToken == null || refreshToken.isBlank() || refreshToken.startsWith("PASTE_")) {
            throw new IllegalStateException(
                "gmail.refresh-token not configured. Get one from https://developers.google.com/oauthplayground");
        }

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", props.getClientId());
        params.add("client_secret", props.getClientSecret());
        params.add("refresh_token", refreshToken);
        params.add("grant_type", "refresh_token");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        ResponseEntity<Map> response = restTemplate.postForEntity(
                TOKEN_ENDPOINT, new HttpEntity<>(params, headers), Map.class);

        Map<?, ?> body = response.getBody();
        if (body == null) throw new RuntimeException("Empty refresh response from Google");

        this.accessToken = (String) body.get("access_token");
        Number expiresIn = (Number) body.get("expires_in");
        this.tokenExpiry = System.currentTimeMillis() + expiresIn.longValue() * 1000 - 60_000;
        return this.accessToken;
    }

    @Override
    public boolean isAuthorized() {
        String rt = props.getRefreshToken();
        return rt != null && !rt.isBlank() && !rt.startsWith("PASTE_");
    }
}
