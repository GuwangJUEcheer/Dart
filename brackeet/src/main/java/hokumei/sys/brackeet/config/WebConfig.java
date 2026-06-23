package hokumei.sys.brackeet.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    @Autowired
    private AdminInterceptor adminInterceptor;

    @Autowired
    private AdminAccessInterceptor adminAccessInterceptor;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Auth interceptor — protect all API routes except public ones
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/user/login",
                        "/api/user/register",
                        "/api/product/list",
                        "/api/product/{id}",
                        "/api/payment/webhook",
                        "/api/fortune",
                        "/api/fortune/reading",
                        "/api/analytics/track",
                        "/api/gmail/oauth/status",
                        "/api/gmail/test"
                );

        // IP/手机端访问控制 — 必须在角色校验之前
        registry.addInterceptor(adminAccessInterceptor)
                .addPathPatterns(
                        "/api/product/add",
                        "/api/product/status",
                        "/api/product/update",
                        "/api/product/delete/**",
                        "/api/order/all",
                        "/api/email/test"
                );

        // Admin interceptor — only for admin routes
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns(
                        "/api/product/add",
                        "/api/product/status",
                        "/api/product/update",
                        "/api/product/delete/**",
                        "/api/order/all",
                        "/api/email/test"
                );
    }
}
