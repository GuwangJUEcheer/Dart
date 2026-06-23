package hokumei.sys.brackeet.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {

    private Long id;
    private String username;
    private String password;
    private String nickname;
    private String email;
    /** 角色: user / admin */
    private String role;
    private String token;
    private LocalDateTime createTime;
}
