package hokumei.sys.brackeet.service;

import hokumei.sys.brackeet.common.Result;
import hokumei.sys.brackeet.dto.LoginRequest;
import hokumei.sys.brackeet.dto.RegisterRequest;
import hokumei.sys.brackeet.entity.User;

public interface UserService {

    Result<User> login(LoginRequest request);

    Result<User> register(RegisterRequest request);
}
