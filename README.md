# OsmJPPostalMapCore
[![Publish to Maven Central](https://github.com/yui-Kitamura/OsmJPPostalMapCore/actions/workflows/publish.yml/badge.svg)](https://github.com/yui-Kitamura/OsmJPPostalMapCore/actions/workflows/publish.yml)

OSM固有書式と日本郵便の実体表現を繋ぎます。

## opening_hours
営業時間表示

## collection_times
ポストの収集時刻

## usage
### depend
Maven Central [SonaType.com](https://central.sonatype.com/artifact/pro.eng.yui.oss.osm.lib/jppostalcore/overview)

### in code

```java
import pro.eng.yui.oss.osm.lib.jppostalcore.JpPostalUtil;

...
JpPostalUtil.callMethods(with_args);
```

### OpenStreetMap API and OAuth

通常の編集APIは `api.openstreetmap.org`、OAuthトークン交換は `www.openstreetmap.org` に接続します。通常APIを呼ぶ場合は従来どおり `JpPostalUtil.getOsmApi()` を、トークン交換には `JpPostalUtil.getOsmOAuthApi()` を使用してください。OAuthトークン取得メソッドは `OsmApi` から `OsmOAuthApi` に移動しました。

```java
import pro.eng.yui.oss.osm.lib.jppostalcore.JpPostalUtil;

JpPostalUtil.getOsmOAuthApi().getAccessToken(
        clientId, clientSecret, code, "authorization_code", redirectUri);
```

## 住所の部屋番号タグ
3.4.0から部屋番号は `addr:unit` を使用します。`JpAddress` の `setRoom`、`getRoom`、`isRoomAvail` はそれぞれ `setUnit`、`getUnit`、`isUnitAvail` に変更され、`addr:room` は読み込まれません。
