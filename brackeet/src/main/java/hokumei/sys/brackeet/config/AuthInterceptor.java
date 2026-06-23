package hokumei.sys.brackeet.config;

import tools.jackson.databind.ObjectMapper;
import hokumei.sys.brackeet.common.Result;
import hokumei.sys.brackeet.entity.User;
import hokumei.sys.brackeet.mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private UserMapper userMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendUnauthorized(response, "请先登录");
            return false;
        }

        String token = authHeader.substring(7);
        User user = userMapper.findByToken(token);
        if (user == null) {
            sendUnauthorized(response, "登录已过期，请重新登录");
            return false;
        }

        request.setAttribute("currentUser", user);
        return true;
    }

    private void sendUnauthorized(HttpServletResponse response, String message) throws Exception {
        response.setStatus(401);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(Result.fail(message)));
    }
}
