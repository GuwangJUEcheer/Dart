package hokumei.sys.brackeet.service.impl;

import hokumei.sys.brackeet.common.Result;
import hokumei.sys.brackeet.dto.LoginRequest;
import hokumei.sys.brackeet.dto.RegisterRequest;
import hokumei.sys.brackeet.entity.User;
import hokumei.sys.brackeet.mapper.UserMapper;
import hokumei.sys.brackeet.service.UserService;
import hokumei.sys.brackeet.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public Result<User> login(LoginRequest request) {
        User user = userMapper.findByUsername(request.getUsername());
        if (user == null) {
            return Result.fail("用户不存在");
        }
        String encryptedPassword = MD5Util.encrypt(request.getPassword());
        if (!user.getPassword().equals(encryptedPassword)) {
            return Result.fail("密码错误");
        }

        String token = UUID.randomUUID().toString().replace("-", "");
        userMapper.updateToken(user.getId(), token);
        user.setToken(token);
        user.setPassword(null);
        return Result.success(user);
    }

    @Override
    public Result<User> register(RegisterRequest request) {
        User existing = userMapper.findByUsername(request.getUsername());
        if (existing != null) {
            return Result.fail("用户名已存在");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(MD5Util.encrypt(request.getPassword()));
        user.setNickname(request.getNickname() != null ? request.getNickname() : request.getUsername());
        user.setEmail(request.getEmail());
        userMapper.insert(user);
        user.setPassword(null);
        return Result.success(user);
    }
}
