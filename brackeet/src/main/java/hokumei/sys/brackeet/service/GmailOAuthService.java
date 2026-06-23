package hokumei.sys.brackeet.service;

public interface GmailOAuthService {
    String getAuthorizationUrl();
    void handleCallback(String code) throws Exception;
    String getValidAccessToken() throws Exception;
    boolean isAuthorized();
}
