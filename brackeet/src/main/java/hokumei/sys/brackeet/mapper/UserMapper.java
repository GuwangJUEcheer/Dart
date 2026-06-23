package hokumei.sys.brackeet.mapper;

import hokumei.sys.brackeet.entity.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM users WHERE username = #{username}")
    User findByUsername(String username);

    @Select("SELECT * FROM users WHERE token = #{token}")
    User findByToken(String token);

    @Select("SELECT * FROM users WHERE id = #{id}")
    User findById(Long id);

    @Insert("INSERT INTO users(username, password, nickname, email, role) VALUES(#{username}, #{password}, #{nickname}, #{email}, 'user')")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Update("UPDATE users SET token = #{token} WHERE id = #{id}")
    int updateToken(@Param("id") Long id, @Param("token") String token);
}
