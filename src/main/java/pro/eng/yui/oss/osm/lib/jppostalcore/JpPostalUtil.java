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
import pro.eng.yui.oss.osm.lib.jppostalcore.parser.CollectionTimeParser;
import pro.eng.yui.oss.osm.lib.jppostalcore.parser.OpeningHoursParser;
import pro.eng.yui.oss.osm.lib.jppostalcore.types.*;
import retrofit2.Call;
import retrofit2.Callback;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/** 
 * OSM郵便マップ向け共通処理
 * @author yuiKITAMURA
 * */
public class JpPostalUtil {
    /** 共通タイムゾーン 日本標準時 */
    public static final ZoneId JST = ZoneId.of("Asia/Tokyo");

    private static final Set<LocalDate> HOLIDAYS = Collections.synchronizedSet(new HashSet<>());
    private static final AtomicBoolean holidaysLoaded = new AtomicBoolean(false);
    private static final Properties buildInfo = new Properties();
    private static final OpeningHoursParser openingHoursParser = new OpeningHoursParser();
    private static final CollectionTimeParser collectionTimeParser = new CollectionTimeParser();

    /* initialize */
    static {
        /* prop取得 */
        try (InputStream is = JpPostalUtil.class.getResourceAsStream("/build-config.properties")) {
            buildInfo.load(is);
        } catch (IOException ignore) {
        }

        /* 祝日情報CSV 取得&パース (非同期実行) */
        CompletableFuture.runAsync(JpPostalUtil::loadHolidays);

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
        OkHttpClient osmClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request request = chain.request().newBuilder()
                            .header("User-Agent", "OsmJPPostalMapCore/" +  buildInfo.getProperty("version"))
                            .build();
                    return chain.proceed(request);
                })
                .build();
        Retrofit osmRetrofit  = new Retrofit.Builder()
                .baseUrl("https://api.openstreetmap.org/")
                .client(osmClient)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        osmApi = osmRetrofit.create(OsmApi.class);
        /* DataSourceReference */
        OkHttpClient dsClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request request = chain.request().newBuilder()
                            .header("User-Agent", "OsmJPPostalMapCore/" +  buildInfo.getProperty("version"))
                            .build();
                    return chain.proceed(request);
                })
                .build();
        Retrofit dataRetrofit = new Retrofit.Builder()
                .baseUrl("https://yui-kitamura.github.io/OsmJpPostalMapDataSource/")
                .client(dsClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        dataSourceApi = dataRetrofit.create(DataSourceApi.class);
    }
    
    private JpPostalUtil(){ /* this is a util class */ }
    
    private static void loadHolidays() {
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
                                if (y < currentYear) {
                                    continue; /* 過去年は切り捨て */
                                }
                                int m = Integer.parseInt(parts[1]);
                                int d = Integer.parseInt(parts[2]);
                                HOLIDAYS.add(LocalDate.of(y, m, d));
                            }
                        } catch (Exception ignore) {
                        }
                    }
                }
            }
        } catch (Exception ignore) {
        }
        holidaysLoaded.set(true);
    }

    /** 祝日データのロードが完了しているかを返します */
    public static boolean isHolidaysLoaded() {
        return holidaysLoaded.get();
    }

    /* 祝日判定 */
    /** 今日が日本の祝日であるかを返します
     * @return 今日が日本の祝日の場合<code>true</code>, 祝日ではない場合<code>false</code>
     * */
    public static boolean isHoliday(){
        return isHoliday(LocalDate.now(JST));
    }
    /** 指定日付が日本の祝日であるかを返します
     * @param date 今年以後の指定日
     * @return 指定された日付が日本の祝日の場合<code>true</code>, ただし今年以後のみ判定対象。
     * */
    public static boolean isHoliday(LocalDate date){
        return HOLIDAYS.contains(date);
    }

    /** 今日の曜日を返します。ただし、祝日の場合はPHです */
    public static Days getDays() {
        return getDays(LocalDate.now(JST));
    }
    public static Days getDays(LocalDate date){
        if (isHoliday(date)) {
            return Days.PUBLIC_HOLIDAY;
        }
        int day = date.getDayOfWeek().getValue(); //1:Mo - 7:Su
        Days[] days = Days.values();
        return days[day-1];
    }
    
    /* OverpassAPIコール */
    private static final OverpassApi overpassApi;
    /** 429エラーハンドリング版
     * @param maxRetry 最大再試行回数 min 1
     * @param interval 試行の間隔秒数
     * @return POIリストのCompletableFuture
     * */
    public static CompletableFuture<List<OsmPoi>> callOverpass(String queryBody, int maxRetry, int interval) {
        return callOverpass(queryBody, maxRetry, interval, 60);
    }
    /** 429エラーハンドリング版（timeout指定可能）
     * @param maxRetry 最大再試行回数 min 1
     * @param interval 試行の間隔秒数
     * @return POIリストのCompletableFuture
     * */
    public static CompletableFuture<List<OsmPoi>> callOverpass(String queryBody, int maxRetry, int interval, int timeout) {
        if (maxRetry < 1) {
            CompletableFuture<List<OsmPoi>> future = new CompletableFuture<>();
            future.completeExceptionally(new IllegalArgumentException("maxRetry must be at least 1"));
            return future;
        }
        CompletableFuture<List<OsmPoi>> future = new CompletableFuture<>();
        callOverpassInternal(queryBody, maxRetry, interval, timeout, 0, future);
        return future;
    }

    private static void callOverpassInternal(String queryBody, int maxRetry, int interval, int timeout, int tryCount, CompletableFuture<List<OsmPoi>> future) {
        int currentTimeout = timeout + tryCount * 15;
        callOverpass(queryBody, currentTimeout).whenComplete((res, ex) -> {
            if (ex == null) {
                future.complete(res);
            } else {
                if (tryCount + 1 < maxRetry) {
                    CompletableFuture.runAsync(() -> {
                        try {
                            TimeUnit.SECONDS.sleep(interval);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                        }
                    }).thenRun(() -> callOverpassInternal(queryBody, maxRetry, interval, timeout, tryCount + 1, future));
                } else {
                    future.completeExceptionally(ex);
                }
            }
        });
    }

    /** overpassAPIをコールする
     * @param queryBody OverpassQLの抽出条件文
     * @return OverpassAPIから返ってくるPOIのリストのCompletableFuture
     * */
    public static CompletableFuture<List<OsmPoi>> callOverpass(String queryBody) {
        return callOverpass(queryBody, 60);
    }

    /**
     * @param timeout クエリのサーバサイド処理のタイムアウト秒数
     * @return OverpassAPIから返ってくるPOIのリストのCompletableFuture
     */
    public static CompletableFuture<List<OsmPoi>> callOverpass(String queryBody, int timeout) {
        CompletableFuture<List<OsmPoi>> future = new CompletableFuture<>();
        String query = queryBody;
        if (!queryBody.contains("[out:json]")) {
            query = "[out:json][timeout:" + timeout + "];" + queryBody + "out meta center qt;";
        }

        overpassApi.query(query).enqueue(new Callback<OverpassResponse>() {
            @Override
            public void onResponse(Call<OverpassResponse> call, Response<OverpassResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<OsmPoi> resultPois = new ArrayList<>();
                    for (OverpassResponse.Element element : response.body().getElements()) {
                        double lat = element.getLat();
                        double lon = element.getLon();
                        if ("way".equals(element.getType()) && element.getCenter() != null) {
                            lat = element.getCenter().getLat();
                            lon = element.getCenter().getLon();
                        }
                        resultPois.add(new OsmPoi(
                                element.getId(), lat, lon, element.getType(), element.getTags(), element.getVersion()
                        ));
                    }
                    future.complete(resultPois);
                } else {
                    int code = response.code();
                    if (429 == code) {
                        future.completeExceptionally(new IllegalStateException("HTTP 429" + response.message()));
                    } else if (400 <= code && code < 500) {
                        future.completeExceptionally(new IllegalArgumentException("HTTP " + code + " error: " + response.message()));
                    } else {
                        future.completeExceptionally(new IOException("HTTP " + code + " error: " + response.message()));
                    }
                }
            }

            @Override
            public void onFailure(Call<OverpassResponse> call, Throwable t) {
                future.completeExceptionally(t);
            }
        });
        return future;
    }
    
    /* OSM API コール */
    private static final OsmApi osmApi;
    public static OsmApi getOsmApi(){ return osmApi; }
    /** ChangeSetを開く
     * @return 採番されたChangesetIDのCompletableFuture
     */
    public static CompletableFuture<Long> callOsmCreateChangeset(String accessToken, ChangeSetInfo info) {
        CompletableFuture<Long> future = new CompletableFuture<>();
        String auth = "Bearer " + accessToken;
        String xml = CreateXML.createChangeset(info);

        osmApi.createChangeset(auth, xml).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        future.complete(Long.parseLong(response.body().trim()));
                    } catch (NumberFormatException e) {
                        future.completeExceptionally(e);
                    }
                } else {
                    String errorBody = "";
                    try (ResponseBody body = response.errorBody()) {
                        if (body != null) {
                            errorBody = body.string();
                        }
                    } catch (IOException ignored) {
                    }
                    future.completeExceptionally(new IOException(response.message() + (errorBody.isEmpty() ? "" : ": " + errorBody)));
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                future.completeExceptionally(t);
            }
        });
        return future;
    }

    /** ChangeSetをクローズ（確定）する
     */
    public static CompletableFuture<Void> callOsmCloseChangeset(String accessToken, ChangeSetInfo id) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        String auth = "Bearer " + accessToken;
        osmApi.closeChangeset(auth, id.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    future.complete(null);
                } else {
                    future.completeExceptionally(new IOException(response.message()));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                future.completeExceptionally(t);
            }
        });
        return future;
    }

    /** ChangeSetに追加/編集するPOIを乗せる
     * @param accessToken OSMのTOKEN
     * @param changeSetInfo idを格納してあること
     * @param poi 対象POIの全情報
     */
    public static CompletableFuture<Void> callOsmCreateOrModifyElement(String accessToken, ChangeSetInfo changeSetInfo, OsmPoi poi) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        String auth = "Bearer " + accessToken;
        Call<String> call;
        if (poi.getVer() == 0) {
            String xml = CreateXML.createElement(changeSetInfo, poi);
            call = osmApi.createElement(auth, poi.getType(), xml);
        } else {
            String xml = CreateXML.modifyElement(changeSetInfo, poi);
            call = osmApi.updateElement(auth, poi.getType(), poi.getId(), xml);
        }

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    future.complete(null);
                } else {
                    String errorBody = "";
                    try (ResponseBody body = response.errorBody()) {
                        if (body != null) {
                            errorBody = body.string();
                        }
                    } catch (IOException ignored) {
                    }
                    future.completeExceptionally(new IOException(response.message() + (errorBody.isEmpty() ? "" : ": " + errorBody)));
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                future.completeExceptionally(t);
            }
        });
        return future;
    }

    public static CompletableFuture<Void> callOsmCreateNote(String accessToken, String appName, String noteBody, double lat, double lon) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        String fullNoteBody = noteBody + "\n\n" + appName;
        String auth = accessToken != null && !accessToken.isEmpty() ? "Bearer " + accessToken : null;

        Call<ResponseBody> call;
        if (auth != null) {
            call = osmApi.createNote(auth, lat, lon, fullNoteBody);
        } else {
            call = osmApi.createNote(lat, lon, fullNoteBody);
        }

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    future.complete(null);
                } else {
                    String errorBody = "";
                    try (ResponseBody body = response.errorBody()) {
                        if (body != null) {
                            errorBody = body.string();
                        }
                    } catch (IOException ignored) {
                    }
                    future.completeExceptionally(new IOException(response.message() + (errorBody.isEmpty() ? "" : ": " + errorBody)));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                future.completeExceptionally(t);
            }
        });
        return future;
    }

    /* JpPostalDatasource処理 */
    private static final DataSourceApi dataSourceApi;
    /** 都道府県リスト
     * @return 県名,コード のMapのCompletableFuture
     */
    public static CompletableFuture<Map<String, Integer>> getPrefectures() {
        CompletableFuture<Map<String, Integer>> future = new CompletableFuture<>();
        dataSourceApi.masterPrefJson().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String jsonString = response.body().string();
                        Map<String, Integer> prefectures = new HashMap<>();
                        Gson gson = new Gson();
                        JsonArray jsonArray = gson.fromJson(jsonString, JsonArray.class);

                        for (int i = 0; i < jsonArray.size(); i++) {
                            JsonObject obj = jsonArray.get(i).getAsJsonObject();
                            String name = obj.get("name").getAsString();
                            int code = Integer.parseInt(obj.get("code").getAsString());
                            prefectures.put(name, code);
                        }
                        future.complete(prefectures);
                    } catch (Exception e) {
                        future.completeExceptionally(e);
                    }
                } else {
                    future.completeExceptionally(new IOException(response.message()));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                future.completeExceptionally(t);
            }
        });
        return future;
    }

    /** 都道府県名からコードを返します
     * @return 都道府県コードのCompletableFuture
     */
    public static CompletableFuture<Integer> getPrefecture(String name) {
        return getPrefectures().thenApply(prefs -> prefs.getOrDefault(name, -99));
    }

    /** 都道府県のデータセットをDataSourceから取得します
     * @return POIリストのCompletableFuture
     */
    public static CompletableFuture<List<OsmPoi>> getPoiData(String prefName) {
        return getPrefecture(prefName).thenCompose(prefCode -> {
            if (prefCode < 0) {
                CompletableFuture<List<OsmPoi>> failed = new CompletableFuture<>();
                failed.completeExceptionally(new IllegalArgumentException("都道府県名不正"));
                return failed;
            }
            CompletableFuture<List<OsmPoi>> future = new CompletableFuture<>();
            dataSourceApi.getPrefData(String.format("%02d", prefCode)).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            String jsonString = response.body().string();
                            List<OsmPoi> prefectureDataList = new ArrayList<>();
                            Gson gson = new Gson();
                            JsonObject prefDataObj = gson.fromJson(jsonString, JsonObject.class);
                            JsonArray jsonArray = prefDataObj.get("data").getAsJsonArray();

                            for (int i = 0; i < jsonArray.size(); i++) {
                                JsonObject obj = jsonArray.get(i).getAsJsonObject();
                                prefectureDataList.add(new OsmPoi(obj));
                            }
                            future.complete(prefectureDataList);
                        } catch (Exception e) {
                            future.completeExceptionally(e);
                        }
                    } else {
                        future.completeExceptionally(new IOException(response.message()));
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    future.completeExceptionally(t);
                }
            });
            return future;
        });
    }

    /* opening_hours, collection_times 処理 */
    public static Map<Days, OpeningHoursParser.DaySchedule> decodeOpeningHours(OpeningHours tagValue){
        return openingHoursParser.decode(tagValue);
    }
    public static OpeningHours encodeOpeningHours(Map<Days, OpeningHoursParser.DaySchedule> data){
        return openingHoursParser.encode(data);
    }

    public static Map<Days, CollectionTimeParser.DaySchedule> decodeCollectionTimes(CollectionTimes tagValue){
        return collectionTimeParser.decode(tagValue);
    }
    public static CollectionTimes encodeCollectionTimes(Map<Days, CollectionTimeParser.DaySchedule> data){
        return collectionTimeParser.encode(data);
    }
    
    /* 住所関係処理 */
    /** 設定されているtagから日本の標準的な住所表示を取得する */
    public static String getAddressText(Map<String,String> tags){
        return JpAddress.of(tags).toString();
    }
    public static JpAddress getAddress(Map<String,String> tags){
        return JpAddress.of(tags);
    }


}
