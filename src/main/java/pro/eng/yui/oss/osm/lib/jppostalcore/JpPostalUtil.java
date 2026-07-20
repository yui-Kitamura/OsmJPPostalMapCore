package pro.eng.yui.oss.osm.lib.jppostalcore;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;

/** 
 * OSM郵便マップ向け共通処理
 * @author yuiKITAMURA
 * */
public class JpPostalUtil {

    private static final ZoneId JST = ZoneId.of("Asia/Tokyo");
    private static final Set<LocalDate> HOLIDAYS = new HashSet<>();

    /* initialize */
    static {
        /* 祝日情報CSV 取得&パース */
        int currentYear = LocalDate.now(JST).getYear();
        try {
            URL url = URI.create("https://www8.cao.go.jp/chosei/shukujitsu/syukujitsu.csv").toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), "Shift_JIS"))) {
                String line;
                boolean isHeader = true;
                while ((line = reader.readLine()) != null) {
                    if (isHeader) {
                        isHeader = false;
                        continue;
                    }
                    String[] cols = line.split(",");
                    if (cols.length > 0) {
                        String dateStr = cols[0].trim();
                        try {
                            // CSV format: yyyy/M/d (e.g., 1955/1/1)
                            String[] parts = dateStr.split("/");
                            if (parts.length == 3) {
                                int y = Integer.parseInt(parts[0]);
                                if (y < currentYear) { continue; /* 過去年は切り捨て */ }
                                int m = Integer.parseInt(parts[1]);
                                int d = Integer.parseInt(parts[2]);
                                HOLIDAYS.add(LocalDate.of(y, m, d));
                            }
                        } catch (Exception ignore) {
                        }
                    }
                }
            }
        }catch (Exception ignore) {
        }
    }
    
    private JpPostalUtil(){ /* this is a util class */ }
    
    /* 祝日判定 */
    /** 今日が日本の祝日であるかを返します
     * @return 今日が日本の祝日の場合<code>true</code>, 祝日ではない場合<code>false</code>
     * */
    public static boolean isHoliday(){
        return isHoliday(LocalDate.now());
    }
    /** 指定日付が日本の祝日であるかを返します
     * @param date 今年以後の指定日
     * @return 指定された日付が日本の祝日の場合<code>true</code>, ただし今年以後のみ判定対象。
     * */
    public static boolean isHoliday(LocalDate date){
        return HOLIDAYS.contains(date);
    }

}
