package hokumei.sys.brackeet.service.impl;

import hokumei.sys.brackeet.common.Result;
import hokumei.sys.brackeet.dto.FortuneResult;
import hokumei.sys.brackeet.mapper.FortuneMapper;
import hokumei.sys.brackeet.service.FortuneApiService;
import hokumei.sys.brackeet.service.FortuneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class FortuneServiceImpl implements FortuneService {

    @Autowired
    private FortuneMapper fortuneMapper;

    @Autowired
    private FortuneApiService fortuneApiService;

    private static final String[] CATEGORY_KEYS = {"love", "family", "friendship", "work", "study", "health"};
    private static final Map<String, String> CATEGORY_LABELS = new LinkedHashMap<>();

    static {
        CATEGORY_LABELS.put("love",       "恋愛・愛情");
        CATEGORY_LABELS.put("family",     "家族・親族");
        CATEGORY_LABELS.put("friendship", "友情・人間関係");
        CATEGORY_LABELS.put("work",       "仕事・キャリア");
        CATEGORY_LABELS.put("study",      "学び・成長");
        CATEGORY_LABELS.put("health",     "心身のバランス");
    }

    @Override
    public Result<FortuneResult> getFortune(String birthday) {
        LocalDate date;
        try {
            date = LocalDate.parse(birthday, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (Exception e) {
            return Result.fail("生年月日の形式が正しくありません（yyyy-MM-dd）");
        }

        String zodiac = getZodiac(date.getMonthValue(), date.getDayOfMonth());

        Map<String, Object> lucky = fortuneMapper.findLucky(zodiac);
        if (lucky == null) {
            return Result.fail("星座データが見つかりません");
        }

        List<Map<String, String>> fortunes = new ArrayList<>();
        for (String cat : CATEGORY_KEYS) {
            String msg = fortuneMapper.findMessage(zodiac, cat);
            if (msg != null) {
                Map<String, String> f = new HashMap<>();
                f.put("category", cat);
                f.put("label", CATEGORY_LABELS.get(cat));
                f.put("message", msg);
                fortunes.add(f);
            }
        }

        FortuneResult result = new FortuneResult();
        result.setZodiac(zodiac);
        result.setLucky(lucky);
        result.setFortunes(fortunes);
        result.setDailyMessage(fortuneApiService.getDailyMessage(zodiac));

        return Result.success(result);
    }

    private String getZodiac(int month, int day) {
        MonthDay md = MonthDay.of(month, day);
        if (md.isAfter(MonthDay.of(3, 20)) && !md.isAfter(MonthDay.of(4, 19))) return "牡羊座";
        if (md.isAfter(MonthDay.of(4, 19)) && !md.isAfter(MonthDay.of(5, 20))) return "牡牛座";
        if (md.isAfter(MonthDay.of(5, 20)) && !md.isAfter(MonthDay.of(6, 21))) return "双子座";
        if (md.isAfter(MonthDay.of(6, 21)) && !md.isAfter(MonthDay.of(7, 22))) return "蟹座";
        if (md.isAfter(MonthDay.of(7, 22)) && !md.isAfter(MonthDay.of(8, 22))) return "獅子座";
        if (md.isAfter(MonthDay.of(8, 22)) && !md.isAfter(MonthDay.of(9, 22))) return "乙女座";
        if (md.isAfter(MonthDay.of(9, 22)) && !md.isAfter(MonthDay.of(10, 23))) return "天秤座";
        if (md.isAfter(MonthDay.of(10, 23)) && !md.isAfter(MonthDay.of(11, 21))) return "蠍座";
        if (md.isAfter(MonthDay.of(11, 21)) && !md.isAfter(MonthDay.of(12, 21))) return "射手座";
        if (md.isAfter(MonthDay.of(12, 21)) && !md.isAfter(MonthDay.of(12, 31))) return "山羊座";
        if (md.equals(MonthDay.of(1, 1)) || (md.isAfter(MonthDay.of(1, 1)) && !md.isAfter(MonthDay.of(1, 19)))) return "山羊座";
        if (md.isAfter(MonthDay.of(1, 19)) && !md.isAfter(MonthDay.of(2, 18))) return "水瓶座";
        return "魚座"; // 2/19 - 3/20
    }
}
