package hokumei.sys.brackeet.controller;

import hokumei.sys.brackeet.common.Result;
import hokumei.sys.brackeet.dto.FortuneResult;
import hokumei.sys.brackeet.service.FortuneService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

@RestController
@RequestMapping("/api/fortune")
public class FortuneController {

    @Autowired
    private FortuneService fortuneService;

    /** 生年月日から運勢を取得（公開API） */
    @GetMapping
    public Result<FortuneResult> getFortune(@RequestParam String birthday) {
        return fortuneService.getFortune(birthday);
    }

    /**
     * 三藏算命八字解读 — 爬取 m.sanzang5.com 并解析 GB2312 结果
     * 公开接口，无需登录
     */
    @GetMapping("/reading")
    public Result<?> getReading(
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam int day) {
        try {
            String sanzangUrl = "https://m.sanzang5.com/bazisuanming.php";
            // t=1: 公历, s=9: 巳时(09:00-09:59) as default birth hour
            String submitVal = URLEncoder.encode("开始算命", "GB2312");
            String params = "t=1&y=" + year + "&m=" + month + "&d=" + day + "&s=9&sizhusuanming=" + submitVal;

            URL obj = new URL(sanzangUrl);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setConnectTimeout(10000);
            con.setReadTimeout(10000);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
            con.setRequestProperty("Referer", sanzangUrl);
            con.setDoOutput(true);

            try (OutputStream os = con.getOutputStream()) {
                os.write(params.getBytes("GB2312"));
            }

            byte[] responseBytes;
            try (InputStream is = con.getInputStream()) {
                responseBytes = is.readAllBytes();
            }

            // Decode GB2312-encoded HTML with Jsoup
            Document doc = Jsoup.parse(new ByteArrayInputStream(responseBytes), "GB2312", sanzangUrl);

            // Four pillars line (e.g. "甲寅 丁亥 辛亥 壬午")
            String pillars = doc.select(".focus_1 p strong").text().trim();

            // Reading sections
            List<Map<String, String>> sections = new ArrayList<>();
            for (Element sub : doc.select(".sanzang_subs")) {
                String title   = sub.select(".subs_title").text().trim();
                String content = sub.select(".subs_main").text().trim();
                if (!title.isEmpty() && !content.isEmpty()) {
                    Map<String, String> s = new LinkedHashMap<>();
                    s.put("title",   title);
                    s.put("content", content);
                    sections.add(s);
                }
            }

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("pillars",  pillars);
            result.put("sections", sections);
            return Result.success(result);

        } catch (Exception e) {
            return Result.fail("命理解读获取失败：" + e.getMessage());
        }
    }
}
