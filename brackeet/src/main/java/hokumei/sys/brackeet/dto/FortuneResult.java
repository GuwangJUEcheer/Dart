package hokumei.sys.brackeet.dto;

import java.util.List;
import java.util.Map;

public class FortuneResult {
    private String zodiac;
    private Map<String, Object> lucky;
    private List<Map<String, String>> fortunes;
    /** Today's English horoscope from external API (nullable) */
    private String dailyMessage;

    public String getZodiac() { return zodiac; }
    public void setZodiac(String zodiac) { this.zodiac = zodiac; }

    public Map<String, Object> getLucky() { return lucky; }
    public void setLucky(Map<String, Object> lucky) { this.lucky = lucky; }

    public List<Map<String, String>> getFortunes() { return fortunes; }
    public void setFortunes(List<Map<String, String>> fortunes) { this.fortunes = fortunes; }

    public String getDailyMessage() { return dailyMessage; }
    public void setDailyMessage(String dailyMessage) { this.dailyMessage = dailyMessage; }
}
