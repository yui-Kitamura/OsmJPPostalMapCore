package pro.eng.yui.oss.osm.lib.jppostalcore.api.osm;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface OsmOAuthApi {
    @FormUrlEncoded
    @POST("oauth2/token")
    Call<ResponseBody> getAccessToken(
            @Field("client_id") String clientId,
            @Field("client_secret") String clientSecret,
            @Field("code") String code,
            @Field("grant_type") String grantType,
            @Field("redirect_uri") String redirectUri
    );

    @FormUrlEncoded
    @POST("oauth2/token")
    Call<ResponseBody> getAccessTokenPublic(
            @Field("client_id") String clientId,
            @Field("code") String code,
            @Field("grant_type") String grantType,
            @Field("redirect_uri") String redirectUri
    );
}
