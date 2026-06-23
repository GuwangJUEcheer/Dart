package hokumei.sys.brackeet.mapper;

import hokumei.sys.brackeet.entity.FortuneMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;
import java.util.Map;

@Mapper
public interface FortuneMapper {

    @Select("SELECT content FROM fortune_message WHERE zodiac = #{zodiac} AND category = #{category} LIMIT 1")
    String findMessage(@Param("zodiac") String zodiac, @Param("category") String category);

    @Select("SELECT lucky_stone, lucky_color, lucky_item, lucky_direction, lucky_number, precaution FROM fortune_lucky WHERE zodiac = #{zodiac}")
    Map<String, Object> findLucky(@Param("zodiac") String zodiac);
}
