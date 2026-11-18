package pro.eng.yui.oss.osm.lib.jppostalcore.api.osm;

import okhttp3.FormBody;
import okhttp3.Request;
import org.junit.jupiter.api.Test;
import pro.eng.yui.oss.osm.lib.jppostalcore.JpPostalUtil;

import static org.junit.jupiter.api.Assertions.*;

class OsmApiEndpointTest {
    @Test
    void oauthTokenRequestsUseWwwEndpointAndExpectedFormFields() {
        Request confidentialRequest = JpPostalUtil.getOsmOAuthApi()
                .getAccessToken("client id", "secret&value", "auth code", "authorization_code", "https://example.com/callback")
                .request();
        assertEndpoint(confidentialRequest, "www.openstreetmap.org", "POST", "/oauth2/token");
        FormBody confidentialForm = assertInstanceOf(FormBody.class, confidentialRequest.body());
        assertFormField(confidentialForm, "client_id", "client id");
        assertFormField(confidentialForm, "client_secret", "secret&value");
        assertFormField(confidentialForm, "code", "auth code");
        assertFormField(confidentialForm, "grant_type", "authorization_code");
        assertFormField(confidentialForm, "redirect_uri", "https://example.com/callback");
        assertEquals(5, confidentialForm.size());

        Request publicRequest = JpPostalUtil.getOsmOAuthApi()
                .getAccessTokenPublic("client id", "auth code", "authorization_code", "https://example.com/callback")
                .request();
        assertEndpoint(publicRequest, "www.openstreetmap.org", "POST", "/oauth2/token");
        FormBody publicForm = assertInstanceOf(FormBody.class, publicRequest.body());
        assertFormField(publicForm, "client_id", "client id");
        assertFormField(publicForm, "code", "auth code");
        assertFormField(publicForm, "grant_type", "authorization_code");
        assertFormField(publicForm, "redirect_uri", "https://example.com/callback");
        assertEquals(4, publicForm.size());
        for (int i = 0; i < publicForm.size(); i++) {
            assertNotEquals("client_secret", publicForm.name(i));
        }
    }

    @Test
    void regularApiRequestsUseApiEndpoint() {
        OsmApi api = JpPostalUtil.getOsmApi();

        assertEndpoint(api.getUserDetailsJson("Bearer token").request(),
                "api.openstreetmap.org", "GET", "/api/0.6/user/details.json");
        assertEndpoint(api.createChangeset("Bearer token", "<osm/>").request(),
                "api.openstreetmap.org", "PUT", "/api/0.6/changeset/create");
        assertEndpoint(api.closeChangeset("Bearer token", 123L).request(),
                "api.openstreetmap.org", "PUT", "/api/0.6/changeset/123/close");
        assertEndpoint(api.getElement("node", 456L).request(),
                "api.openstreetmap.org", "GET", "/api/0.6/node/456");
        assertEndpoint(api.createElement("Bearer token", "node", "<node/>").request(),
                "api.openstreetmap.org", "PUT", "/api/0.6/node/create");
        assertEndpoint(api.updateElement("Bearer token", "way", 789L, "<way/>").request(),
                "api.openstreetmap.org", "PUT", "/api/0.6/way/789");
        assertEndpoint(api.createNote(35.0, 139.0, "note").request(),
                "api.openstreetmap.org", "POST", "/api/0.6/notes");
        assertEndpoint(api.createNote("Bearer token", 35.0, 139.0, "note").request(),
                "api.openstreetmap.org", "POST", "/api/0.6/notes");
    }

    private static void assertEndpoint(Request request, String host, String method, String path) {
        assertEquals("https", request.url().scheme());
        assertEquals(host, request.url().host());
        assertEquals(method, request.method());
        assertEquals(path, request.url().encodedPath());
    }

    private static void assertFormField(FormBody form, String name, String expectedValue) {
        boolean found = false;
        for (int i = 0; i < form.size(); i++) {
            if (name.equals(form.name(i))) {
                assertEquals(expectedValue, form.value(i));
                found = true;
            }
        }
        assertTrue(found, "Missing form field: " + name);
    }
}
