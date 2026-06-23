package hokumei.sys.brackeet.controller;

import hokumei.sys.brackeet.common.Result;
import hokumei.sys.brackeet.dto.LoginRequest;
import hokumei.sys.brackeet.dto.RegisterRequest;
import hokumei.sys.brackeet.entity.User;
import hokumei.sys.brackeet.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<User> login(@RequestBody LoginRequest request) {
        return userService.login(request);
    }

    @PostMapping("/register")
    public Result<User> register(@RequestBody RegisterRequest request) {
        return userService.register(request);
    }
}
