package hokumei.sys.brackeet.controller;

import hokumei.sys.brackeet.common.Result;
import hokumei.sys.brackeet.entity.PageView;
import hokumei.sys.brackeet.mapper.PageViewMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private PageViewMapper pageViewMapper;

    /**
     * 追踪页面停留时间 — 公开接口，无需登录
     * Body: { "page": "/products", "durationSeconds": 45, "sessionId": "uuid" }
     */
    @PostMapping("/track")
    public Result<Void> track(@RequestBody PageView pageView) {
        if (pageView.getPage() == null || pageView.getPage().isBlank()) {
            return Result.fail("page is required");
        }
        if (pageView.getDurationSeconds() == null || pageView.getDurationSeconds() < 0) {
            pageView.setDurationSeconds(0);
        }
        pageViewMapper.insert(pageView);
        return Result.success(null);
    }

    /** 管理员接口：今日统计 */
    @GetMapping("/today")
    public Result<List<Map<String, Object>>> today() {
        return Result.success(pageViewMapper.statsToday());
    }

    /** 管理员接口：近7天统计 */
    @GetMapping("/week")
    public Result<List<Map<String, Object>>> week() {
        return Result.success(pageViewMapper.statsLast7Days());
    }

    /** 管理员接口：全时段总计 */
    @GetMapping("/total")
    public Result<List<Map<String, Object>>> total() {
        return Result.success(pageViewMapper.statsTotal());
    }
}
