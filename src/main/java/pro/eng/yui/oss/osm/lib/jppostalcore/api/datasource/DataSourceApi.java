package pro.eng.yui.oss.osm.lib.jppostalcore.api.datasource;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface DataSourceApi {
    
    @GET("data/master/pref.json")
    Call<ResponseBody> masterPrefJson();

    @GET("data/jPostal_｛code}.json")
    Call<ResponseBody> getPrefData(@Path("code") String code);
}
