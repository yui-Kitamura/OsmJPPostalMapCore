package pro.eng.yui.oss.osm.lib.jppostalcore.parser;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AddressParserTest {

    @Test
    void getView() {
        Map<String, String> tags = new HashMap<>();
        AddressParser parser = new AddressParser();
        assertEquals("", parser.getView(tags));
    }

    @Test
    void parseTokyoAddress() {
        // 東京都 千代田区 丸の内二丁目 7-2
        Map<String, String> tags = new HashMap<>();
        tags.put("addr:province", "東京都");
        tags.put("addr:city", "千代田区");
        tags.put("addr:neighbourhood", "丸の内二丁目");
        tags.put("addr:block_number", "7");
        tags.put("addr:housenumber", "2");

        AddressParser parser = new AddressParser();
        String result = parser.getView(tags);

        assertNotNull(result);
        assertEquals("東京都 千代田区 丸の内二丁目 7-2", result);
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

        AddressParser parser = new AddressParser();
        String result = parser.getView(tags);

        assertNotNull(result);
        assertEquals("大阪府 大阪市 浪速区 日本橋西一丁目 1-3 アニメイトビル 1F", result);
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

        AddressParser parser = new AddressParser();
        String result = parser.getView(tags);

        assertNotNull(result);
        assertEquals("山梨県 中巨摩郡 昭和町 押越 542-2", result);
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

        AddressParser parser = new AddressParser();
        String result = parser.getView(tags);

        assertNotNull(result);
        assertEquals("山形県 鶴岡市 北京田 字下鳥ノ巣 6-1", result);
    }

}