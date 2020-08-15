package info.bitrich.xchangestream.huobi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class UrlParamsBuilder {
  private final Map<String, String> paramsMap = new HashMap();

  public static UrlParamsBuilder build() {
    return new UrlParamsBuilder();
  }

  private UrlParamsBuilder() {}

  public UrlParamsBuilder putToUrl(String name, String value) {
    paramsMap.put(name, value);
    return this;
  }

  public String buildSignature() {
    Map<String, String> map = new TreeMap<>(paramsMap);
    StringBuilder head = new StringBuilder();
    return AppendUrl(map, head);
  }

  private String AppendUrl(Map<String, String> map, StringBuilder stringBuilder) {
    for (Map.Entry<String, String> entry : map.entrySet()) {
      if (!("").equals(stringBuilder.toString())) {
        stringBuilder.append("&");
      }
      stringBuilder.append(entry.getKey());
      stringBuilder.append("=");
      stringBuilder.append(urlEncode(entry.getValue()));
    }
    return stringBuilder.toString();
  }

  public String buildUrlToJsonString() {
    ObjectMapper mapper = new ObjectMapper();
    try {
      return mapper.writeValueAsString(paramsMap);
    } catch (JsonProcessingException e) {
      return null;
    }
  }

  /**
   * 使用标准URL Encode编码。注意和JDK默认的不同，空格被编码为%20而不是+。
   *
   * @param s String字符串
   * @return URL编码后的字符串
   */
  private static String urlEncode(String s) {
    try {
      return URLEncoder.encode(s, "UTF-8").replaceAll("\\+", "%20");
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException("invalid input", e);
    }
  }
}
