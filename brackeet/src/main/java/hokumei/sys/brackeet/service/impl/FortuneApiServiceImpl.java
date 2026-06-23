package hokumei.sys.brackeet.service.impl;

import hokumei.sys.brackeet.service.FortuneApiService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class FortuneApiServiceImpl implements FortuneApiService {

    // Zodiac name (Japanese) → English sign name for the external API
    private static final Map<String, String> ZODIAC_TO_SIGN = Map.ofEntries(
        Map.entry("牡羊座", "Aries"),
        Map.entry("牡牛座", "Taurus"),
        Map.entry("双子座", "Gemini"),
        Map.entry("蟹座",   "Cancer"),
        Map.entry("獅子座", "Leo"),
        Map.entry("乙女座", "Virgo"),
        Map.entry("天秤座", "Libra"),
        Map.entry("蠍座",   "Scorpio"),
        Map.entry("射手座", "Sagittarius"),
        Map.entry("山羊座", "Capricorn"),
        Map.entry("水瓶座", "Aquarius"),
        Map.entry("魚座",   "Pisces")
    );

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Calls https://horoscope-app-api.vercel.app/api/v1/get-horoscope/daily
     * Returns today's horoscope string, or null on any failure.
     */
    @Override
    @SuppressWarnings("unchecked")
    public String getDailyMessage(String zodiac) {
        try {
            String sign = ZODIAC_TO_SIGN.getOrDefault(zodiac, "Aries");
            String url  = "https://horoscope-app-api.vercel.app/api/v1/get-horoscope/daily?sign=" + sign + "&day=today";

            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response == null) return null;

            Object dataObj = response.get("data");
            if (dataObj instanceof Map) {
                Map<String, Object> data = (Map<String, Object>) dataObj;
                Object msg = data.get("horoscope_data");
                return msg instanceof String ? (String) msg : null;
            }
        } catch (Exception ignored) {
            // External API is optional — silently fall back to null
        }
        return null;
    }
}
