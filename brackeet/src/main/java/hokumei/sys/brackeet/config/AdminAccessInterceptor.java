package hokumei.sys.brackeet.config;

import hokumei.sys.brackeet.common.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import tools.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;

/**
 * 管理员接口访问控制：仅允许 IP 白名单或手机端访问。
 * 白名单在 application.properties 的 admin.allowed-ips 中维护，逗号分隔。
 */
@Component
public class AdminAccessInterceptor implements HandlerInterceptor {

    @Value("${admin.allowed-ips:}")
    private String allowedIpsConfig;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final List<String> MOBILE_UA_KEYWORDS = List.of(
            "Mobile", "Android", "iPhone", "iPad", "Windows Phone", "HarmonyOS"
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String clientIp = resolveClientIp(request);
        if (isAllowedIp(clientIp) || isMobileRequest(request)) {
            return true;
        }

        response.setStatus(403);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(
                Result.fail("访问受限：仅允许授权 IP 或手机端访问管理后台")
        ));
        return false;
    }

    private boolean isAllowedIp(String ip) {
        if (allowedIpsConfig == null || allowedIpsConfig.isBlank()) return false;
        List<String> allowed = Arrays.stream(allowedIpsConfig.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
        return allowed.contains(ip);
    }

    private boolean isMobileRequest(HttpServletRequest request) {
        String ua = request.getHeader("User-Agent");
        if (ua == null) return false;
        return MOBILE_UA_KEYWORDS.stream().anyMatch(ua::contains);
    }

    /** 依次从代理头、真实IP头、TCP远端地址中取客户端IP */
    private String resolveClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isBlank() && !"unknown".equalsIgnoreCase(ip)) {
            return ip.split(",")[0].trim();
        }
        ip = request.getHeader("X-Real-IP");
        if (ip != null && !ip.isBlank() && !"unknown".equalsIgnoreCase(ip)) {
            return ip.trim();
        }
        return request.getRemoteAddr();
    }
}
