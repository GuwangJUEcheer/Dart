package hokumei.sys.brackeet.controller;

import hokumei.sys.brackeet.common.Result;
import hokumei.sys.brackeet.service.GmailEmailSender;
import hokumei.sys.brackeet.service.GmailOAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestController
@RequestMapping("/api/gmail")
public class GmailOAuthController {

    @Autowired
    private GmailOAuthService gmailOAuthService;

    @Autowired
    private GmailEmailSender gmailEmailSender;

    @GetMapping("/oauth/status")
    public Result<Map<String, Object>> status() {
        return Result.success(Map.of("authorized", gmailOAuthService.isAuthorized()));
    }

    @PostMapping("/test")
    public Result<String> sendTestEmail(@RequestParam(defaultValue = "") String to) {
        if (to.isBlank()) {
            return Result.fail("请填写收件人邮箱 ?to=xxx@example.com");
        }
        try {
            String html = "<div style='font-family:sans-serif;padding:24px'>" +
                    "<h2 style='color:#8e44ad'>✅ Brackeet Gmail 连接测试</h2>" +
                    "<p>如果您看到此邮件，说明 Gmail OAuth2 配置成功。</p>" +
                    "<p style='color:#888'>发送时间: " +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) +
                    "</p></div>";
            gmailEmailSender.send(to, "[Brackeet] Gmail 连接测试", html);
            return Result.success("测试邮件已发送至 " + to);
        } catch (Exception e) {
            return Result.fail("发送失败: " + e.getMessage());
        }
    }
}
