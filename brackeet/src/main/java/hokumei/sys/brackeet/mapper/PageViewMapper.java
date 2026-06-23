package hokumei.sys.brackeet.mapper;

import hokumei.sys.brackeet.entity.PageView;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface PageViewMapper {

    @Insert("INSERT INTO page_view (page, duration_seconds, session_id) VALUES (#{page}, #{durationSeconds}, #{sessionId})")
    void insert(PageView pageView);

    /** 最近7天各页面 UV（按天聚合） */
    @Select("""
        SELECT page,
               DATE(create_time) AS date,
               COUNT(*) AS visits,
               AVG(duration_seconds) AS avgDuration
        FROM page_view
        WHERE create_time >= NOW() - INTERVAL '7 days'
        GROUP BY page, DATE(create_time)
        ORDER BY date DESC, visits DESC
        """)
    List<Map<String, Object>> statsLast7Days();

    /** 今日各页面访问量 + 平均停留时长 */
    @Select("""
        SELECT page,
               COUNT(*) AS visits,
               ROUND(AVG(duration_seconds)) AS avgDuration
        FROM page_view
        WHERE DATE(create_time) = CURRENT_DATE
        GROUP BY page
        ORDER BY visits DESC
        """)
    List<Map<String, Object>> statsToday();

    /** 各页面总访问量（全时段 top 10） */
    @Select("""
        SELECT page,
               COUNT(*) AS visits,
               ROUND(AVG(duration_seconds)) AS avgDuration
        FROM page_view
        GROUP BY page
        ORDER BY visits DESC
        LIMIT 10
        """)
    List<Map<String, Object>> statsTotal();
}
