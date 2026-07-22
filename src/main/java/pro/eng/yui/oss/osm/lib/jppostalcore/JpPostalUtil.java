package pro.eng.yui.oss.osm.lib.jppostalcore;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import pro.eng.yui.oss.osm.lib.jppostalcore.api.datasource.DataSourceApi;
import pro.eng.yui.oss.osm.lib.jppostalcore.api.osm.ChangeSetInfo;
import pro.eng.yui.oss.osm.lib.jppostalcore.api.osm.CreateXML;
import pro.eng.yui.oss.osm.lib.jppostalcore.api.osm.OsmApi;
import pro.eng.yui.oss.osm.lib.jppostalcore.api.overpass.OverpassApi;
import pro.eng.yui.oss.osm.lib.jppostalcore.api.overpass.OverpassResponse;
import pro.eng.yui.oss.osm.lib.jppostalcore.parser.OpeningHoursParser;
import pro.eng.yui.oss.osm.lib.jppostalcore.types.*;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;

/** 
 * OSM郵便マップ向け共通処理
 * @author yuiKITAMURA
 * */
public class JpPostalUtil {

    private static final ZoneId JST = ZoneId.of("Asia/Tokyo");
    private static final Set<LocalDate> HOLIDAYS = new HashSet<>();
    private static Properties buildInfo = new Properties();

    /* initialize */
    static {
        /* prop取得 */
        try (InputStream is = JpPostalUtil.class.getResourceAsStream("/build-config.properties")) {
            buildInfo.load(is);
        }catch (IOException ignore){}
        
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
        /* OverpassAPI */
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request request = chain.request().newBuilder()
                            .header("User-Agent", "OsmJPPostalMapCore/" +  buildInfo.getProperty("version"))
                            .build();
                    return chain.proceed(request);
                })
                .build();
        Retrofit overpassRetrofit = new Retrofit.Builder()
                .baseUrl("https://overpass-api.de/api/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        overpassApi = overpassRetrofit.create(OverpassApi.class);
        /* OpenStreetMap API */
        Retrofit osmRetrofit  = new Retrofit.Builder()
                .baseUrl("https://www.openstreetmap.org/")
                .client(client)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        osmApi = osmRetrofit.create(OsmApi.class);
        /* DataSourceReference */
        Retrofit dataRetrofit = new Retrofit.Builder()
                .baseUrl("https://yui-kitamura.github.io/OsmJpPostalMapDataSource/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        dataSourceApi = dataRetrofit.create(DataSourceApi.class);
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
    
    /* OverpassAPIコール */
    private static final OverpassApi overpassApi;
    /** 429エラーハンドリング版
     * @param maxRetry 最大再試行回数 min 1
     * @param interval 試行の間隔秒数
     * @throws IOException 致命的失敗
     * @throws IllegalStateException 最大試行回数到達してもなお成功しなかった場合
     * */
    public static List<OsmPoi> callOverpass(String queryBody, int maxRetry, int interval) throws IOException,IllegalStateException {
        for (int tryCount = 0; tryCount<maxRetry; tryCount++) {
            try {
                return callOverpass(queryBody);
            }catch (IllegalStateException|IOException e) {
                try {
                    TimeUnit.SECONDS.sleep(interval);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new IOException("Thread interrupted during retry", ie);
                }
                continue;
            }
        }
        throw new IllegalStateException("Max retry count exceeded");
    }

    /** overpassAPIをコールする
     * @param queryBody OverpassQLの抽出条件文
     * @return OverpassAPIから返ってきたPOIのリスト 
     * */
    public static List<OsmPoi> callOverpass(String queryBody) throws IOException,IllegalStateException {
        String query = "[out:json][timeout:60];" + queryBody + "out meta center qt;";

        List<OsmPoi> resultPois = new ArrayList<>();
        try {
            Response<OverpassResponse> response = overpassApi.query(query).execute();
            if (response.isSuccessful() && response.body() != null) {
                for (OverpassResponse.Element element : response.body().getElements()) {
                    double lat = element.getLat();
                    double lon = element.getLon();
                    if ("way".equals(element.getType()) && element.getCenter() != null) {
                        lat = element.getCenter().getLat();
                        lon = element.getCenter().getLon();
                    }

                    resultPois.add(new OsmPoi(
                        element.getId(), lat, lon, element.getType(), element.getTags(),element.getVersion()
                    ));
                }
            } else {
                int code = response.code();
                if (500 <= code) {
                    throw new IOException("HTTP " + code + " error: " + response.message());
                }
                if (429 == code) {
                    throw new IllegalStateException("HTTP 429" + response.message());
                }
                if (400 <= code) {
                    throw new IllegalArgumentException("HTTP " + code + " error: " + response.message());
                }
                throw new RuntimeException(response.message());
            }
        } catch (IOException e) {
            throw e;
        }

        return resultPois;
    }
    
    /* OSM API コール */
    private static final OsmApi osmApi;
    public static OsmApi getOsmApi(){ return osmApi; }
    /** ChangeSetを開く
     * @return 採番されたChangesetID
     * @throws IOException 通信失敗時などfail */
    public static long callOsmCreateChangeset(String accessToken, ChangeSetInfo info) throws IOException{
        String auth = "Bearer " + accessToken;
        String xml = CreateXML.createChangeset(info);

        Response<String> res = osmApi.createChangeset(auth, xml).execute();
        if (res.isSuccessful() && res.body() != null) {
            return Long.parseLong(res.body().trim());
        }
        throw new IOException(res.message());

    }
    /** ChangeSetをクローズ（確定）する
     * @throws IllegalStateException クローズ失敗時 */
    public static void callOsmCloseChangeset(String accessToken, ChangeSetInfo id){
        String auth = "Bearer " + accessToken;
        try {
            osmApi.closeChangeset(auth, id.getId()).execute();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
    /** ChangeSetに追加/編集するPOIを乗せる
     * @param accessToken OSMのTOKEN
     * @param changeSetInfo idを格納してあること
     * @param poi 対象POIの全情報 */
    public static void callOsmCreateOrModifyElement(String accessToken, ChangeSetInfo changeSetInfo, OsmPoi poi) throws IOException{
        String auth = "Bearer " + accessToken;
        if (poi.getVer() == 0) {
            String xml = CreateXML.createElement(changeSetInfo, poi);
            Response<String> res = osmApi.createElement(auth, poi.getType(), xml).execute();
            if (res.isSuccessful() == false){ throw new IOException(res.message()); }
        }else {
            String xml = CreateXML.modifyElement(changeSetInfo, poi);
            Response<String> res = osmApi.updateElement(auth, poi.getType(), poi.getId(), xml).execute();
            if (res.isSuccessful() == false){ throw new IOException(res.message()); }
        }
    }
    
    /* JpPostalDatasource処理 */
    private static final DataSourceApi dataSourceApi;
    /** 都道府県リスト
     * @return 県名,コード のMap */
    public static Map<String, Integer> getPrefectures() {
        Map<String, Integer> prefectures = new HashMap<>();
        try{    
            Response<ResponseBody> res = dataSourceApi.masterPrefJson().execute();
            if (res.isSuccessful() == false) { throw new IOException(res.message()); }

            String jsonString = res.body().string();
            Gson gson = new Gson();
            JsonArray jsonArray = gson.fromJson(jsonString, JsonArray.class);

            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject obj = jsonArray.get(i).getAsJsonObject();
                String name = obj.get("name").getAsString();
                int code = Integer.parseInt(obj.get("code").getAsString());
                prefectures.put(name, code);
            }
        } catch (IOException ignore) { }
        return prefectures;
    }
    /** 都道府県名からコードを返します */
    public static int getPrefecture(String name){
        return getPrefectures().getOrDefault(name, -99);
    }
    /** 都道府県のデータセットをDataSourceから取得します */
    public static List<OsmPoi> getPoiData(String prefName){
        int prefCode = getPrefecture(prefName);
        if (prefCode < 0){ throw new IllegalArgumentException("都道府県名不正"); }
        List<OsmPoi> prefectureDataList = new ArrayList<>();

        try{
            Response<ResponseBody> res = dataSourceApi.getPrefData(String.format("%02d",prefCode)).execute();
            if (res.isSuccessful() == false) { throw new IOException(res.message()); }

            String jsonString = res.body().string();
            Gson gson = new Gson();
            JsonObject prefDataObj = gson.fromJson(jsonString, JsonObject.class);
            JsonArray jsonArray = prefDataObj.get("data").getAsJsonArray();

            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject obj = jsonArray.get(i).getAsJsonObject();
                prefectureDataList.add(new OsmPoi(obj));
            }
        }catch (IOException ignore) { }
        return prefectureDataList;
    }

    /* opening_hours, collection_times 処理 */
    public static Map<Days, OpeningHoursParser.DaySchedule> decode(OpeningHours tagValue){
        OpeningHoursParser parser = new OpeningHoursParser();
        return parser.decode(tagValue);
    }
    public static OpeningHours encode(Map<Days, OpeningHoursParser.DaySchedule> data){
        OpeningHoursParser parser = new OpeningHoursParser();
        return parser.encode(data);
    }
    
}
