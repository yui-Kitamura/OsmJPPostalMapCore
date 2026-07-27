package pro.eng.yui.oss.osm.lib.jppostalcore.api.datasource;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface DataSourceApi {
    
    @GET("master/pref.json")
    Call<ResponseBody> masterPrefJson();

    @GET("master/sub/{prefCode}.json")
    Call<ResponseBody> masterSubJson(@Path("prefCode") String prefCode);

    @GET("master/boundary.json")
    Call<ResponseBody> masterBoundaryJson();

    @GET("data/jPostal_{code}.json")
    Call<ResponseBody> getPrefData(@Path("code") String code);

    @GET("data/jPostal_{code}_{subCode}.json")
    Call<ResponseBody> getSubAreaData(@Path("code") String code, @Path("subCode") String subCode);
}
