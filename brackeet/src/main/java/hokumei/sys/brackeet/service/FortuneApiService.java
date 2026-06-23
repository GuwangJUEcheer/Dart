package hokumei.sys.brackeet.service;

/**
 * 外部占いAPIから毎日のホロスコープメッセージを取得するサービス
 * Source: horoscope-app-api.vercel.app (free, no API key required)
 */
public interface FortuneApiService {

    /**
     * @param zodiac 星座名（日本語）例: "牡羊座"
     * @return 今日の英語ホロスコープメッセージ、取得失敗時は null
     */
    String getDailyMessage(String zodiac);
}
