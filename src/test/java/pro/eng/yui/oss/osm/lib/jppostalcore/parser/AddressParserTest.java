package pro.eng.yui.oss.osm.lib.jppostalcore.parser;

import org.junit.jupiter.api.Test;
import pro.eng.yui.oss.osm.lib.jppostalcore.types.JpAddress;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AddressParserTest {

    @Test
    void testToString() {
        Map<String, String> tags = new HashMap<>();
        assertEquals("", JpAddress.of(tags).toString());
    }

    @Test
    void parseTokyoAddress() {
        // 東京都 千代田区 丸の内二丁目 7-2
        Map<String, String> tags = new HashMap<>();
        tags.put("addr:postcode", "100-8994");
        tags.put("addr:province", "東京都");
        tags.put("addr:city", "千代田区");
        tags.put("addr:neighbourhood", "丸の内二丁目");
        tags.put("addr:block_number", "7");
        tags.put("addr:housenumber", "2");
        tags.put("addr:housename", "JPタワー");

        String result = JpAddress.of(tags).toString();

        assertNotNull(result);
        assertEquals("〒100-8994 東京都千代田区丸の内二丁目7-2 JPタワー", result);
    }

    @Test
    void parseOsakaAddressWithBuilding() {
        // 大阪府 大阪市 浪速区 日本橋西一丁目 1-3 アニメイトビル 1F
        Map<String, String> tags = new HashMap<>();
        tags.put("addr:province", "大阪府");
        tags.put("addr:city", "大阪市");
        tags.put("addr:suburb", "浪速区");
        tags.put("addr:neighbourhood", "日本橋西一丁目");
        tags.put("addr:block_number", "1");
        tags.put("addr:housenumber", "3");
        tags.put("addr:housename", "アニメイトビル");
        tags.put("addr:floor", "1F");

        String result = JpAddress.of(tags).toString();

        assertNotNull(result);
        assertEquals("大阪府大阪市浪速区日本橋西一丁目1-3 アニメイトビル 1F", result);
    }

    @Test
    void parseYamanashiAddress() {
        // 山梨県 中巨摩郡 昭和町 押越 542-2
        Map<String, String> tags = new HashMap<>();
        tags.put("addr:province", "山梨県");
        tags.put("addr:county", "中巨摩郡");
        tags.put("addr:city", "昭和町");
        tags.put("addr:neighbourhood", "押越");
        tags.put("addr:block_number", "542");
        tags.put("addr:housenumber", "2");

        String result = JpAddress.of(tags).toString();

        assertNotNull(result);
        assertEquals("山梨県中巨摩郡昭和町押越542-2", result);
    }

    @Test
    void parseYamagataAddressWithFieldNotation() {
        // 山形県 鶴岡市 北京田 字下鳥ノ巣 6-1
        Map<String, String> tags = new HashMap<>();
        tags.put("addr:province", "山形県");
        tags.put("addr:city", "鶴岡市");
        tags.put("addr:quarter", "北京田");
        tags.put("addr:neighbourhood", "字下鳥ノ巣");
        tags.put("addr:block_number", "6");
        tags.put("addr:housenumber", "1");

        JpAddress address = JpAddress.of(tags);
        String result = address.toString();

        assertNotNull(result);
        assertEquals("山形県鶴岡市北京田字下鳥ノ巣6-1", result);
    }

}