package pro.eng.yui.oss.osm.lib.jppostalcore.api.overpass;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface OverpassApi {
    @FormUrlEncoded
    @POST("interpreter")
    Call<OverpassResponse> query(@Field("data") String data);
}
