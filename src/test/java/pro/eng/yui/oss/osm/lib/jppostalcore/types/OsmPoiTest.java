package pro.eng.yui.oss.osm.lib.jppostalcore.types;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OsmPoiTest {
    
    @Test
    void testConstructFromJson(){
        String jsonStr = "{ \"id\": 13513951199, \"lat\": 40.4901056, \"lon\": 139.9501736, \"tags\": { \"addr:block_number\": \"25\", \"addr:city\": \"深浦町\", \"addr:county\": \"西津軽郡\", \"addr:housenumber\": \"2\", \"addr:neighbourhood\": \"大間越宮崎浜\", \"addr:postcode\": \"038-2208\", \"addr:province\": \"青森県\", \"amenity\": \"post_office\", \"name\": \"大間越簡易郵便局\",\"phone\": \"+81-173-78-2121\" }, \"type\": \"node\", \"ver\": 1 }";
        JsonObject jsonObj = JsonParser.parseString(jsonStr).getAsJsonObject();
        OsmPoi created = assertDoesNotThrow(()->{ return new OsmPoi(jsonObj); });
        assertEquals(13513951199L, created.getId());
        assertEquals(40.4901056d, created.getLat());
        assertEquals(139.9501736, created.getLon());
        assertEquals("node", created.getType());
        assertEquals(1L, created.getVer());
        assertEquals("大間越簡易郵便局", created.getTag("name"));
    }

}