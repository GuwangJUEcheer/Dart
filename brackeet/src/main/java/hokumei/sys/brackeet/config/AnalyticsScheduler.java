package hokumei.sys.brackeet.config;

import hokumei.sys.brackeet.mapper.PageViewMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 页面访问统计定时任务
 * 每天凌晨2点生成昨日报告并写入日志（可扩展为写入聚合表）
 */
@Component
public class AnalyticsScheduler {

    private static final Logger log = LoggerFactory.getLogger(AnalyticsScheduler.class);

    @Autowired
    private PageViewMapper pageViewMapper;

    /** 每小时打印一次今日实时数据 */
    @Scheduled(cron = "0 0 * * * *")
    public void hourlyReport() {
        List<Map<String, Object>> stats = pageViewMapper.statsToday();
        if (stats.isEmpty()) return;
        log.info("=== 本日ページ閲覧統計 ({}) ===", LocalDate.now());
        for (Map<String, Object> row : stats) {
            log.info("  {} → {}回 / 平均{}秒", row.get("page"), row.get("visits"), row.get("avgDuration"));
        }
    }

    /** 每天凌晨2点打印过去7天汇总 */
    @Scheduled(cron = "0 0 2 * * *")
    public void dailySummary() {
        List<Map<String, Object>> stats = pageViewMapper.statsLast7Days();
        log.info("=== 過去7日間ページ閲覧サマリー ===");
        for (Map<String, Object> row : stats) {
            log.info("  [{}] {} → {}回 / 平均{}秒",
                    row.get("date"), row.get("page"), row.get("visits"), row.get("avgDuration"));
        }
    }
}
